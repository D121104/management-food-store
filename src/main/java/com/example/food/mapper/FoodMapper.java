package com.example.food.mapper;

import com.example.food.dto.response.food.FoodResponse;
import com.example.food.entity.FoodEntity;
import org.mapstruct.Mapper;

@Mapper
public interface FoodMapper {
    FoodResponse toFoodResponse(FoodEntity foodEntity);
}
