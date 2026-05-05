package com.example.food.dto.response.auth;

import com.example.food.dto.response.user.UserDetailResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private UserDetailResponse userDetailResponse;
}
