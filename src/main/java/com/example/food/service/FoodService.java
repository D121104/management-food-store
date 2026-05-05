package com.example.food.service;

import com.example.food.dto.request.food.FilterFoodRequest;
import com.example.food.dto.response.food.FoodResponse;
import com.example.food.dto.response.ingredient.IngredientResponse;
import com.example.food.entity.*;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.mapper.FoodMapper;
import com.example.food.repository.FoodRepository;
import com.example.food.repository.IngredientRepository;
import com.example.food.repository.UserFoodLikeRepository;
import com.example.food.repository.UserRepository;
import com.example.food.security.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final UserFoodLikeRepository userFoodLikeRepository;
    private final UserRepository userRepository;
    private final FoodMapper foodMapper;
    private final IngredientRepository ingredientRepository;

    @Transactional(readOnly = true)
    public Page<FoodResponse> searchByFilter(Pageable pageable, FilterFoodRequest filterFoodRequest, String accessToken) {
        Page<FoodEntity> foodEntityPage = foodRepository.findAllByFilter(
                filterFoodRequest.getCategoryDetailIds(), filterFoodRequest.getRating(), pageable
        );

        return returnCommonResponse(foodEntityPage, accessToken);
    }

    @Transactional(readOnly = true)
    public Page<FoodResponse> searchByName(String name, Pageable pageable, String accessToken) {
        Page<FoodEntity> foodEntityPage = foodRepository.findAllByNameContainingIgnoreCase(name, pageable);

        return returnCommonResponse(foodEntityPage, accessToken);
    }

    @Transactional(readOnly = true)
    public Page<FoodResponse> getFoodsLike(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<FoodEntity> foodEntityPage = foodRepository.findLikedFoods(userId, pageable);

        return returnCommonResponse(foodEntityPage, accessToken);
    }

    private Page<FoodResponse> returnCommonResponse(Page<FoodEntity> foodEntityPage, String accessToken) {

        Set<Long> foodIds = findUserFoodLikeIds(accessToken);

        return foodEntityPage.map(
                foodEntity -> {
                    FoodResponse foodResponse = returnFoodResponse(foodEntity, foodIds);
                    return foodResponse;
                }
        );
    }

    private Set<Long> findUserFoodLikeIds(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<UserFoodLikeEntity> userFoodLikeIds = userFoodLikeRepository.findAllByUserId(userId);

        return userFoodLikeIds.stream()
                .map(UserFoodLikeEntity::getFoodId).collect(Collectors.toSet());
    }

    private FoodResponse returnFoodResponse(FoodEntity foodEntity, Set<Long> foodIds) {
        FoodResponse foodResponse = FoodResponse.builder()
                .id(foodEntity.getId())
                .name(foodEntity.getName())
                .urlImage(foodEntity.getImage())
                .price(foodEntity.getPrice())
                .description(foodEntity.getDescription())
                .avgRating(
                        foodEntity.getAvgRating() == null ? null : foodEntity.getAvgRating()
                )
                .totalComment(foodEntity.getTotalComments())
                .hasLiked(
                        foodIds.contains(foodEntity.getId()) ? true : false
                )
                .quantity(foodEntity.getQuantity())
                .totalBought(foodEntity.getTotalBought())
                .totalLikes(foodEntity.getTotalLikes())
                .build();
        return foodResponse;
    }

    @Transactional
    public void likeFood(String accessToken, Long foodId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        FoodEntity foodEntity = foodRepository.findById(foodId).orElseThrow(
                () -> new AppException(ErrorCode.FOOD_NOT_FOUND)
        );

        if (userFoodLikeRepository.existsByUserIdAndFoodId(userEntity.getId(), foodId)) {
            throw new AppException(ErrorCode.FOOD_ALREADY_LIKED);
        }

        UserFoodLikeEntity userFoodLikeEntity = UserFoodLikeEntity.builder()
                .foodId(foodId)
                .userId(userId)
                .build();

        foodEntity.setTotalLikes(foodEntity.getTotalLikes() + 1);
        foodRepository.save(foodEntity);

        userFoodLikeRepository.save(userFoodLikeEntity);
    }

    @Transactional
    public void unlikeFood(String accessToken, Long userFoodLikeId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if (userFoodLikeRepository.existsByUserIdAndId(userId, userFoodLikeId)) {
            userFoodLikeRepository.deleteById(userFoodLikeId);
        }
    }

    @Transactional(readOnly = true)
    public FoodResponse getFoodDetailById(Long foodId, String accessToken) {
        Set<Long> foodIds = findUserFoodLikeIds(accessToken);

        FoodEntity foodEntity = foodRepository.findById(foodId).orElseThrow(
                () -> new AppException(ErrorCode.FOOD_NOT_FOUND)
        );

        List<IngredientEntity> ingredientEntities = ingredientRepository.findAllByFood(foodId);

        List<IngredientResponse> ingredientResponses = new ArrayList<>();

        for (IngredientEntity ingredientEntity : ingredientEntities) {
            IngredientResponse ingredientResponse = IngredientResponse.builder()
                    .id(ingredientEntity.getId())
                    .name(ingredientEntity.getName())
                    .price(ingredientEntity.getPrice())
                    .build();
            ingredientResponses.add(ingredientResponse);
        }

        return FoodResponse.builder()
                .id(foodEntity.getId())
                .name(foodEntity.getName())
                .urlImage(foodEntity.getImage())
                .price(foodEntity.getPrice())
                .description(foodEntity.getDescription())
                .avgRating(
                        foodEntity.getAvgRating() == null ? null : foodEntity.getAvgRating()
                )
                .totalComment(foodEntity.getTotalComments())
                .totalLikes(foodEntity.getTotalLikes())
                .totalBought(foodEntity.getTotalBought())
                .hasLiked(
                        foodIds.contains(foodEntity.getId()) ? true : false
                )
                .quantity(foodEntity.getQuantity())
                .ingredientResponse(ingredientResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<FoodResponse> getFoodsByCategory(Long categoryId, Pageable pageable, String accessToken) {
        Page<FoodEntity> foodEntityPage = foodRepository.findByCategoryId(categoryId, pageable);

        if (foodEntityPage.isEmpty() || Objects.isNull(foodEntityPage.getContent())) {
            return Page.empty();
        }

        Set<Long> foodIds = findUserFoodLikeIds(accessToken);

        return foodEntityPage.map(
                foodEntity -> {
                    FoodResponse foodResponse = returnFoodResponse(foodEntity, foodIds);
                    return foodResponse;
                }
        );
    }

    // fallback
    private List<FoodResponse> getDefaultFoods(Set<Long> foodIds) {
        List<FoodEntity> foodEntities = foodRepository.findAll(PageRequest.of(0, 10)).getContent();

        List<FoodResponse> foodResponses = new ArrayList<>();

        for (FoodEntity foodEntity : foodEntities) {
            FoodResponse foodResponse = returnFoodResponse(foodEntity, foodIds);
            foodResponses.add(foodResponse);
        }
        return foodResponses;
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> getBestSeller(String accessToken) {
        Set<Long> foodIds = findUserFoodLikeIds(accessToken);

        List<FoodEntity> foodEntities =
                foodRepository.findTop10ByOrderByTotalBoughtDesc();

        if (foodEntities.isEmpty()) {
            return getDefaultFoods(foodIds);
        }

        if (foodEntities.size() < 10) {
            int remain = 10 - foodEntities.size();

            List<Long> existedIds = foodEntities.stream()
                    .map(FoodEntity::getId)
                    .toList();

            List<FoodEntity> moreFoods =
                    foodRepository.findFoodsExcludeIds(existedIds, PageRequest.of(0, remain));

            foodEntities.addAll(moreFoods);
        }

        List<FoodResponse> foodResponses = new ArrayList<>();

        for (FoodEntity foodEntity : foodEntities) {
            foodResponses.add(returnFoodResponse(foodEntity, foodIds));
        }

        return foodResponses;
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> getRecommendFoods(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Set<Long> foodIds = findUserFoodLikeIds(accessToken);

        List<FoodEntity> recommendFoods =
                foodRepository.findTopFoodBoughtOfUser(userId, PageRequest.of(0, 10));

        if (recommendFoods.size() >= 10) {
            return mapToResponse(recommendFoods, foodIds);
        }

        int remain = 10 - recommendFoods.size();

        List<Long> existedIds = recommendFoods.stream()
                .map(FoodEntity::getId)
                .toList();

        List<FoodEntity> defaultFoods =
                foodRepository.findRandomFoodsExcludeIds(existedIds, PageRequest.of(0, remain));

        recommendFoods.addAll(defaultFoods);

        return mapToResponse(recommendFoods, foodIds);
    }

    private List<FoodResponse> mapToResponse(List<FoodEntity> foods, Set<Long> foodIds) {
        List<FoodResponse> responses = new ArrayList<>();

        for (FoodEntity food : foods) {
            responses.add(returnFoodResponse(food, foodIds));
        }

        return responses;
    }


    @Transactional
    public void updateFoodTotalBoughtAndQuantity(Long foodId, int quantity) {
        FoodEntity food = foodRepository.findById(foodId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));
        food.setTotalBought(food.getTotalBought() + quantity);
        foodRepository.save(food);
    }

    @Transactional
    public void removeLikeFoodByUserIdAndFoodId(String accessToken, Long foodId) {
        Long userId =  TokenHelper.getUserIdFromToken(accessToken);
        if (!userFoodLikeRepository.existsByUserIdAndFoodId(userId, foodId)) {
            throw new AppException(ErrorCode.FOOD_NOT_LIKE);
        }

        userFoodLikeRepository.deleteByUserIdAndFoodId(userId, foodId);
    }

}
