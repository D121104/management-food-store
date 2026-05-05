package com.example.food.service;

import com.example.food.dto.request.cart.CartItemIngredientRequest;
import com.example.food.dto.request.cart.CartItemRequest;
import com.example.food.dto.request.cart.UpdateCartItemRequest;
import com.example.food.dto.response.cart.CartItemResponse;
import com.example.food.dto.response.cart.CartResponse;
import com.example.food.dto.response.food.FoodResponse;
import com.example.food.dto.response.ingredient.IngredientResponse;
import com.example.food.entity.*;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.repository.*;
import com.example.food.security.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class CartItemService {
    private final UserRepository userRepository;
    private final IngredientFoodRepository ingredientFoodRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;
    private final CartItemIngredientRepository cartItemIngredientRepository;

    @Transactional
    public CartItemResponse addCartItem(String accessToken, CartItemRequest cartItemRequest) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (!foodRepository.existsById(cartItemRequest.getFoodId())) {
            throw new AppException(ErrorCode.FOOD_NOT_FOUND);
        }

        if (!enoughQuantity(cartItemRequest)) {
            throw new AppException(ErrorCode.QUANTITY_NOT_ENOUGH);
        }

        CartItemEntity cartItem = CartItemEntity.builder()
                .userId(userId)
                .foodId(cartItemRequest.getFoodId())
                .quantity(cartItemRequest.getQuantity())
                .build();

        cartItemRepository.save(cartItem);

        List<CartItemIngredientRequest> cartItemIngredientRequests = cartItemRequest.getIngredients();
        for  (CartItemIngredientRequest cartItemIngredientRequest : cartItemIngredientRequests) {
            if (!ingredientFoodRepository.existsByFoodIdAndIngredientId(cartItemRequest.getFoodId(), cartItemIngredientRequest.getIngredientId())) {
                throw new AppException(ErrorCode.INGREDIENT_FOOD_NOT_FOUND);
            }
            CartItemIngredientEntity cartItemIngredientEntity = CartItemIngredientEntity.builder()
                    .cartItemId(cartItem.getId())
                    .ingredientId(cartItemIngredientRequest.getIngredientId())
                    .build();

            cartItemIngredientRepository.save(cartItemIngredientEntity);
        }

        return returnCartItemResponse(cartItem);
    }

    private boolean enoughQuantity(CartItemRequest cartItemRequest) {
        FoodEntity foodEntity = foodRepository.findById(cartItemRequest.getFoodId()).orElseThrow(
                () -> new AppException(ErrorCode.FOOD_NOT_FOUND)
        );

        if (foodEntity.getQuantity() < cartItemRequest.getQuantity()) {
            return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public CartResponse getCartItems(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<CartItemEntity> cartItemEntities = cartItemRepository.findAllByUserId(userId);
        List<CartItemResponse> cartItemResponses = new ArrayList<>();

        for (CartItemEntity cartItemEntity : cartItemEntities) {
            cartItemResponses.add(returnCartItemResponse(cartItemEntity));
        }
        long totalPrice = caculateTotalPrice(cartItemResponses);
        return CartResponse.builder()
                .userId(userId)
                .carts(cartItemResponses)
                .totalPrice(totalPrice)
                .build();
    }

    @Transactional
    public CartItemResponse updateCartItems(String accessToken, UpdateCartItemRequest updateCartItemRequest) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        CartItemEntity cartItem = cartItemRepository.findById(updateCartItemRequest.getCartItemId())
                .orElseThrow(() -> new  AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!userId.equals(cartItem.getUserId())) {
            throw new AppException(ErrorCode.NOT_PERMISSION_UPDATE_CART);
        }

        cartItem.setQuantity(updateCartItemRequest.getQuantity());
        cartItemRepository.save(cartItem);

        return returnCartItemResponse(cartItem);
    }

    @Transactional
    public CartItemResponse deleteCartItems(String accessToken, Long cartItemId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        CartItemEntity cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new  AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!userId.equals(cartItem.getUserId())) {
            throw new AppException(ErrorCode.NOT_PERMISSION_UPDATE_CART);
        }

        cartItemIngredientRepository.deleteAllByCartItemId(cartItemId);

        cartItemRepository.deleteById(cartItemId);
        return returnCartItemResponse(cartItem);

    }


    private CartItemResponse returnCartItemResponse(CartItemEntity cartItem) {


        FoodEntity food = foodRepository.findById(cartItem.getFoodId())
                .orElseThrow(() ->new AppException(ErrorCode.FOOD_NOT_FOUND));


        FoodResponse foodResponse = FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .urlImage(food.getImage())
                .price(food.getPrice())
                .quantity(food.getQuantity())
                .description(food.getDescription())
                .avgRating(food.getAvgRating())
                .totalComment(food.getTotalComments())
                .totalBought(food.getTotalBought())
                .totalLikes(food.getTotalLikes())
                .build();

        List<CartItemIngredientEntity> cartItemIngredientEntities = cartItemIngredientRepository.findAllByCartItemId(cartItem.getId());
        List<IngredientResponse> ingredientResponses = new ArrayList<>();

        for  (CartItemIngredientEntity cartItemIngredient : cartItemIngredientEntities) {
            IngredientEntity ingredient = ingredientRepository.findById(cartItemIngredient.getIngredientId())
                    .orElseThrow(() -> new  AppException(ErrorCode.INGREDIENT_NOT_FOUND));

            IngredientResponse ingredientResponse = IngredientResponse.builder()
                    .id(ingredient.getId())
                    .name(ingredient.getName())
                    .price(ingredient.getPrice())
                    .build();
            ingredientResponses.add(ingredientResponse);
        }


        CartItemResponse cartItemResponse = CartItemResponse.builder()
                .id(cartItem.getId())
                .quantity(cartItem.getQuantity())
                .food(foodResponse)
                .ingredients(ingredientResponses)
                .build();
        return cartItemResponse;
    }

    private Long caculateTotalPrice(List<CartItemResponse> cartItems) {
        long totalPrice = 0L;
        for (CartItemResponse cartItem : cartItems) {
            long itemPrice = 0L;
            for (IngredientResponse ingredient : cartItem.getIngredients()) {
                itemPrice += ingredient.getPrice();
            }
            itemPrice += cartItem.getFood().getPrice();
            itemPrice *= cartItem.getQuantity();
            totalPrice += itemPrice;
        }
        return totalPrice;
    }
}
