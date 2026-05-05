package com.example.food.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String image;
    private String description;
    private int quantity;
    private int price;
    @Column(name = "avg_rating", nullable = true)
    private Double avgRating;
    private int totalComments;
    private int totalLikes;
    private int totalBought;
}
