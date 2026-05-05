package com.example.food.dto.response.ingredient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IngredientResponse {
    private Long id;
    private String name;
    private int price;
}
