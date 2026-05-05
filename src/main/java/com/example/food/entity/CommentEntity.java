package com.example.food.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_comment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long foodId;
    private String comment;
    private float rating;
    private LocalDateTime createdAt;
}
