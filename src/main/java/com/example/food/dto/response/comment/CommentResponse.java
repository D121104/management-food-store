package com.example.food.dto.response.comment;

import com.example.food.dto.response.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentResponse {
    private Long userId;
    private String fullName;
    private String urlImage;
    private String comment;
    private float rating;
    private LocalDateTime createdAt;
    @JsonProperty("isMe")
    private boolean isMe;
}
