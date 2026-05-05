package com.example.food.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderDetailRequest {
    private Long foodId;
    private int quantity;
    List<OrderDetailIngredientRequest> orderDetailIngredients;
}
