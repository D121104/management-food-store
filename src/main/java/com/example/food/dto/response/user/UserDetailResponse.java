package com.example.food.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDetailResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private String imageUrl;
    private String role;
}
