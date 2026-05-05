package com.example.food.dto.response.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Password cũ không được để trống")
    private String oldPassword;
    @NotBlank(message = "Password mới không được để trống")
    private String newPassword;
    @NotBlank(message = "Xác nhận lại password không được để trống")
    private String confirmNewPassword;
}
