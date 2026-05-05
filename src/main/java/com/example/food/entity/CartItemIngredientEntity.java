package com.example.food.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_cart_item_ingredient")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemIngredientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cartItemId;
    private Long ingredientId;
}
