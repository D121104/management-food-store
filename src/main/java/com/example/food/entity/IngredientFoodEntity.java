package com.example.food.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_ingredient_food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngredientFoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long foodId;
    private Long ingredientId;
}
