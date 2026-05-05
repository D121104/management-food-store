package com.example.food.dto.response.food;

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
public class FoodResponse {
    private Long id;
    private String name;
    private String urlImage;
    private String description;
    private int quantity;
    private int price;
    private Double avgRating;
    private int totalComment;
    private int totalLikes;
    private boolean hasLiked;
    private int totalBought;
    private List<IngredientResponse> ingredientResponse;
}
