package com.example.food.service;

import com.example.food.common.OrderStatus;
import com.example.food.common.PaymentStatus;
import com.example.food.dto.request.order.*;
import com.example.food.dto.response.ingredient.IngredientResponse;
import com.example.food.dto.response.order.OrderDetailResponse;
import com.example.food.dto.response.order.OrderResponse;
import com.example.food.entity.*;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.mapper.OrderDetailMapper;
import com.example.food.mapper.OrderMapper;
import com.example.food.repository.*;
import com.example.food.security.TokenHelper;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OrderService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailService orderDetailService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final FoodService foodService;
    private final IngredientRepository ingredientRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemIngredientRepository cartItemIngredientRepository;
    private final OrderDetailIngredientRepository orderDetailIngredientRepository;

    @Transactional
    public OrderResponse createOrder(String accessToken ,OrderRequest orderRequest) {

        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        AddressEntity address = addressRepository.findById(orderRequest.getAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        // tao order
        OrderEntity order = OrderEntity.builder()
                .status(OrderStatus.ACTIVE)
                .userId(user.getId())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .address(address.getAddress())
                .deliveryPrice(orderRequest.getDeliveryPrice())
                .totalPrice(0L)
                .paymentStatus(PaymentStatus.UNPAID)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .build();

        order =  orderRepository.save(order);

        Long totalPrice = order.getTotalPrice();

        // tao order detail
        List<OrderDetailRequest>  orderDetailRequests = orderRequest.getOrderDetails();
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();

        for (OrderDetailRequest orderDetailRequest : orderDetailRequests) {
            Long ingredientPrice = 0L;

            FoodEntity food = foodRepository.findById(orderDetailRequest.getFoodId())
                    .orElseThrow(() ->new AppException(ErrorCode.FOOD_NOT_FOUND));

            int quantity = orderDetailRequest.getQuantity();


            // Kiểm tra quantity
            if (quantity > food.getQuantity()) {
                throw new AppException(ErrorCode.QUANTITY_NOT_ENOUGH);
            }

            food.setQuantity(food.getQuantity() - quantity);
            foodRepository.save(food);

            OrderDetailEntity orderDetail = OrderDetailEntity.builder()
                    .orderId(order.getId())
                    .foodId(orderDetailRequest.getFoodId())
                    .quantity(quantity)
                    .build();

            orderDetail = orderDetailRepository.save(orderDetail);

            // tao order detail ingredient
            List<OrderDetailIngredientRequest>  orderDetailIngredientRequests = orderDetailRequest.getOrderDetailIngredients();
            List<IngredientResponse> ingredientResponseList = new ArrayList<>();
            for  (OrderDetailIngredientRequest orderDetailIngredientRequest : orderDetailIngredientRequests) {
                OrderDetailIngredientEntity orderDetailIngredient = OrderDetailIngredientEntity.builder()
                        .orderDetailId(orderDetail.getId())
                        .ingredientId(orderDetailIngredientRequest.getIngredientId())
                        .build();
                orderDetailIngredientRepository.save(orderDetailIngredient);

                Long ingredientId = orderDetailIngredient.getIngredientId();

                IngredientEntity ingredient = ingredientRepository.findById(orderDetailIngredient.getIngredientId())
                                .orElseThrow(() ->new AppException(ErrorCode.INGREDIENT_NOT_FOUND));

                ingredientPrice += ingredient.getPrice();

                ingredientResponseList.add(commonIngredientResponse(ingredient));
            }

            // xoa vat pham trong gio hang
            CartItemEntity cartItem = cartItemRepository.findByUserIdAndFoodId(user.getId(), food.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
            cartItemIngredientRepository.deleteAllByCartItemId(cartItem.getId());
            cartItemRepository.delete(cartItem);

            long price =  ((long) food.getPrice() + ingredientPrice) * quantity;
            totalPrice += price;

            orderDetail.setPrice(price);
            orderDetail = orderDetailRepository.save(orderDetail);
            orderDetailResponses.add(orderDetailService.commonOrderDetailResponse(orderDetail));
        }

        order.setTotalPrice(totalPrice + orderRequest.getDeliveryPrice());
        orderRepository.save(order);

//        Long orderId = order.getId();
//     scheduler.schedule(()->updateStatusAndOrderFoodTotalBought(orderId), 1, TimeUnit.MINUTES);

        OrderResponse orderResponse = returnCommonResponse(order);
        orderResponse.setOrderDetails(orderDetailResponses);

        return orderResponse;
    }

    @Transactional
    public OrderResponse cancelOrder(String accessToken, CancelOrderRequest cancelOrderRequest) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        OrderEntity order = orderRepository.findById(cancelOrderRequest.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.CANCEL_ORDER_FAILED);
        }

        if (order.getStatus() != OrderStatus.ACTIVE) {
            throw new  AppException(ErrorCode.ORDER_CANT_BE_CANCELED);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        reverseQuantity(order);
        return returnCommonResponse(order);
    }

    private void reverseQuantity(OrderEntity order) {
        List<OrderDetailEntity> orderDetailEntities = orderDetailRepository
                .findAllByOrderId(order.getId());

        Map<Long, FoodEntity> foodEntities = foodRepository.findAllByIdIn(
                orderDetailEntities.stream().map(OrderDetailEntity::getFoodId).toList()
        ).stream().collect(Collectors.toMap(FoodEntity::getId, Function.identity()));

        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            FoodEntity foodEntity = foodEntities.get(orderDetailEntity.getFoodId());
            foodEntity.setQuantity(foodEntity.getQuantity() + orderDetailEntity.getQuantity());
            foodRepository.save(foodEntity);
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(String accessToken ,Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        Page<OrderEntity> orderEntityPage = orderRepository.findAllByUserId(userId, pageable);
        return returnCommonResponseList(orderEntityPage);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserIdAndStatus(String accessToken, Pageable pageable, OrderStatus status) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        Page<OrderEntity> orderEntityPage = orderRepository.findAllByUserIdAndStatus(userId, status, pageable);
        return returnCommonResponseList(orderEntityPage);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String accessToken, Long  orderId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        OrderEntity order;

        if  (orderRepository.findById(orderId).isPresent()) {
             order = orderRepository.findById(orderId).get();
        } else {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        return returnCommonResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(String accessToken ,UpdateStatusRequest updateStatusRequest) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        OrderEntity order = orderRepository.findById(updateStatusRequest.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setStatus(updateStatusRequest.getOrderStatus());
        if  (order.getStatus() == OrderStatus.COMPLETED) {
            order = updateStatusAndOrderFoodTotalBought(order.getId());
        }
        orderRepository.save(order);
        return returnCommonResponse(order);
    }

    @Transactional
    public void updateOrderPaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        OrderEntity  order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        int updateRows = orderRepository.updateStatusIfChanged(orderId, paymentStatus);
        if (updateRows != 1) {
            throw new AppException(ErrorCode.UPDATE_ORDER_PAYMENT_STATUS_FAILED);
        }

    }

    private OrderResponse returnCommonResponse(OrderEntity orderEntity) {
        List<OrderDetailResponse> orderDetailResponses = orderDetailService.findAllByOrderId(orderEntity.getId());

        return OrderResponse.<OrderResponse>builder()
                .id(orderEntity.getId())
                .userId(orderEntity.getUserId())
                .latitude(orderEntity.getLatitude())
                .longitude(orderEntity.getLongitude())
                .address(orderEntity.getAddress())
                .deliveryPrice(orderEntity.getDeliveryPrice())
                .totalPrice(orderEntity.getTotalPrice())
                .status(orderEntity.getStatus())
                .orderDetails(orderDetailResponses)
                .createdAt(orderEntity.getCreatedAt())
                .build();
    }


    private Page<OrderResponse> returnCommonResponseList(Page<OrderEntity> orderEntityPage) {
        return orderEntityPage.map(
                orderEntity -> {
                    List<OrderDetailResponse> orderDetailResponses = orderDetailService.findAllByOrderId(orderEntity.getId());

                    return OrderResponse.builder()
                            .id(orderEntity.getId())
                            .userId(orderEntity.getUserId())
                            .latitude(orderEntity.getLatitude())
                            .longitude(orderEntity.getLongitude())
                            .address(orderEntity.getAddress())
                            .deliveryPrice(orderEntity.getDeliveryPrice())
                            .totalPrice(orderEntity.getTotalPrice())
                            .status(orderEntity.getStatus())
                            .orderDetails(orderDetailResponses)
                            .createdAt(orderEntity.getCreatedAt())
                            .build();
                }
        );
    }

    public IngredientResponse commonIngredientResponse(IngredientEntity ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .price(ingredient.getPrice())
                .build();
    }

    @Transactional
    protected OrderEntity updateStatusAndOrderFoodTotalBought(Long orderId) {
        OrderEntity  order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        orderRepository.save(order);
        List<OrderDetailEntity> orderDetailList = orderDetailRepository.findAllByOrderId(orderId);
        for (OrderDetailEntity orderDetail : orderDetailList) {
            FoodEntity food = foodRepository.findById(orderDetail.getFoodId())
                            .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));
            foodService.updateFoodTotalBoughtAndQuantity(food.getId(), orderDetail.getQuantity());
        }

        return order;
    }

}
