package com.example.food.dto.response.user;

import com.example.food.dto.response.auth.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private TokenResponse token;
}
