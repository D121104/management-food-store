package com.example.food.service;

import com.example.food.dto.response.food.FoodResponse;
import com.example.food.dto.response.ingredient.IngredientResponse;
import com.example.food.dto.response.order.OrderDetailResponse;
import com.example.food.entity.*;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.mapper.FoodMapper;
import com.example.food.mapper.OrderDetailMapper;
import com.example.food.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientFoodRepository ingredientFoodRepository;
    private final OrderDetailIngredientRepository orderDetailIngredientRepository;
    private final FoodMapper foodMapper;

    @Transactional(readOnly = true)
    public List<OrderDetailResponse> findAllByOrderId(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        List<OrderDetailEntity> orderDetailEntities = orderDetailRepository.findAllByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses  = new ArrayList<>();
        for (OrderDetailEntity orderDetail : orderDetailEntities) {

            FoodEntity food = foodRepository.findById(orderDetail.getFoodId())
                    .orElseThrow(() ->new AppException(ErrorCode.FOOD_NOT_FOUND));

            FoodResponse foodResponse = FoodResponse.builder()
                    .id(food.getId())
                    .name(food.getName())
                    .urlImage(food.getImage())
                    .price(food.getPrice())
                    .description(food.getDescription())
                    .avgRating(food.getAvgRating())
                    .totalComment(food.getTotalComments())
                    .totalBought(food.getTotalBought())
                    .totalLikes(food.getTotalLikes())
                    .build();

            List<OrderDetailIngredientEntity> orderDetailIngredients = orderDetailIngredientRepository.findAllByOrderDetailId(orderDetail.getId());
            List<IngredientResponse> ingredientResponsesList  = new ArrayList<>();
            for (OrderDetailIngredientEntity orderDetailIngredient : orderDetailIngredients) {
                IngredientEntity ingredient = ingredientRepository.findById(orderDetailIngredient.getIngredientId())
                        .orElseThrow(() ->new AppException(ErrorCode.INGREDIENT_NOT_FOUND));
                ingredientResponsesList.add(commonIngredientResponse(ingredient));
            }

            OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                    .id(orderDetail.getId())
                    .food(foodResponse)
                    .ingredients(ingredientResponsesList)
                    .quantity(orderDetail.getQuantity())
                    .price(orderDetail.getPrice())
                    .build();
            orderDetailResponses.add(orderDetailResponse);
        }

        return orderDetailResponses;
    }

    public IngredientResponse commonIngredientResponse(IngredientEntity ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .price(ingredient.getPrice())
                .build();
    }

    public OrderDetailResponse commonOrderDetailResponse(OrderDetailEntity orderDetail) {
        FoodEntity food = foodRepository.findById(orderDetail.getFoodId())
                .orElseThrow(() ->new AppException(ErrorCode.FOOD_NOT_FOUND));

        FoodResponse foodResponse = FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .urlImage(food.getImage())
                .price(food.getPrice())
                .description(food.getDescription())
                .avgRating(food.getAvgRating())
                .totalComment(food.getTotalComments())
                .totalBought(food.getTotalBought())
                .totalLikes(food.getTotalLikes())
                .build();

        List<OrderDetailIngredientEntity> orderDetailIngredients = orderDetailIngredientRepository.findAllByOrderDetailId(orderDetail.getId());
        List<IngredientResponse> ingredientResponsesList  = new ArrayList<>();
        for (OrderDetailIngredientEntity orderDetailIngredient : orderDetailIngredients) {
            IngredientEntity ingredient = ingredientRepository.findById(orderDetailIngredient.getIngredientId())
                    .orElseThrow(() ->new AppException(ErrorCode.INGREDIENT_NOT_FOUND));
            ingredientResponsesList.add(commonIngredientResponse(ingredient));
        }

        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .food(foodResponse)
                .ingredients(ingredientResponsesList)
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .build();
    }
}
