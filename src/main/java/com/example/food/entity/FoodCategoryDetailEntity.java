package com.example.food.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_food_category_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodCategoryDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long foodId;
    private Long categoryDetailId;
}
