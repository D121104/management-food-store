package com.example.food.dto.response.cart;

import com.example.food.entity.CartItemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartResponse {
    private Long userId;
    private Long totalPrice;
    private List<CartItemResponse> carts;
}
