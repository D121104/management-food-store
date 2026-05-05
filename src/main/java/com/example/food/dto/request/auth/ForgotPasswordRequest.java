package com.example.food.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForgotPasswordRequest {
    @NotEmpty(message = "Email không được để trống")
    private String email;
    @NotBlank(message = "Mật khẩu mới không được để trống")
    private String newPassword;
    @NotBlank(message = "Mật khẩu xác nhận không được để trống")
    private String confirmNewPassword;
    @NotBlank(message = "Mã xác nhận không được để trống")
    private String otp;
}
