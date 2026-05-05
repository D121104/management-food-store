package com.example.food.dto.request.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequest {
    private Long orderId;
    private Long foodId;
    private String comment;
    private float rating;
}
