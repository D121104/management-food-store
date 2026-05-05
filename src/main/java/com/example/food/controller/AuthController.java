package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.dto.request.auth.ForgotPasswordRequest;
import com.example.food.dto.request.auth.LogInRequest;
import com.example.food.dto.request.auth.RefreshRequest;
import com.example.food.dto.request.auth.SignUpRequest;
import com.example.food.dto.response.auth.ChangePasswordRequest;
import com.example.food.dto.response.auth.TokenResponse;
import com.example.food.dto.response.user.UserResponse;
import com.example.food.security.TokenHelper;
import com.example.food.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Đăng nhập")
    @PostMapping("/log-in")
    public ApiResponse<UserResponse> logIn(@RequestBody @Valid LogInRequest logInRequest){
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Đăng nhập thành công")
                .result(authService.logIn(logInRequest))
                .build();
    }

    @Operation(summary = "Đăng ký")
    @PostMapping("/sign-up")
    public ApiResponse<UserResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest){
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Đăng kí thành công")
                .result(authService.signUp(signUpRequest))
                .build();
    }

    @Operation(summary = "Đăng nhập với vân tay")
    @PostMapping("/biometric")
    public ApiResponse<UserResponse> loginWithBiometric(@RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Đăng nhập thành công")
                .result(authService.loginWithBiometric(accessToken))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        return ApiResponse.<TokenResponse>builder()
                .code(200)
                .message("Cấp token mới thành công")
                .result(authService.refreshToken(refreshRequest))
                .build();
    }

    @Operation(summary = "Đổi mật khẩu")
    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestHeader("Authorization") String accessToken,
                                         @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        authService.changePassword(changePasswordRequest, userId);
        return ApiResponse.builder()
                .code(200)
                .message("Thay đổi mật khẩu thành công")
                .build();
    }

    @Operation(summary = "Gửi mã OTP")
    @PostMapping("/OTP/{email}")
    public ApiResponse<?> sendOTP(@PathVariable String email) {

        authService.sendOTP(email);
        return ApiResponse.builder()
                .code(200)
                .message("Cấp OTP thành công")
                .build();
    }

    @Operation(summary = "Quên mật khẩu")
    @PostMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest);

        return ApiResponse.builder()
                .code(200)
                .message("Mật khẩu được đặt lại thành công")
                .build();
    }
}
