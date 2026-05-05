package com.example.food.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_user_food_like")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFoodLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long foodId;
}
