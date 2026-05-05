package com.example.food.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_category_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long categoryId;
}
