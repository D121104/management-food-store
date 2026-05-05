package com.example.food.dto.request.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartItemRequest {
    private Long foodId;
    private int quantity;
    private List<CartItemIngredientRequest> ingredients;
}
