package com.example.food.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_ingredient")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngredientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
}
