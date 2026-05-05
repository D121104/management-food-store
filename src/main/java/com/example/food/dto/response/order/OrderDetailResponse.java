package com.example.food.dto.response.order;

import com.example.food.dto.response.food.FoodResponse;
import com.example.food.dto.response.ingredient.IngredientResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderDetailResponse {
    private Long id;
    private FoodResponse food;
    private List<IngredientResponse> ingredients;
    private int quantity;
    private Long price;
}
