package com.example.food.service;


import com.example.food.dto.request.auth.ForgotPasswordRequest;
import com.example.food.dto.request.auth.LogInRequest;
import com.example.food.dto.request.auth.RefreshRequest;
import com.example.food.dto.request.auth.SignUpRequest;
import com.example.food.dto.response.auth.ChangePasswordRequest;
import com.example.food.dto.response.auth.TokenResponse;
import com.example.food.dto.response.user.UserResponse;
import com.example.food.entity.UserEntity;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.repository.UserRepository;
import com.example.food.security.TokenHelper;
import com.example.food.service.async.OTPEvent;
import com.example.food.service.redis.PresenceService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CustomerService customerService;
    private final ApplicationEventPublisher eventPublisher;
    private final PresenceService presenceService;
    @Transactional
    public UserResponse signUp(SignUpRequest signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        signUpRequest.setPassword(BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt()));
        UserEntity userEntity = UserEntity.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .fullName(signUpRequest.getFullName())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .dateOfBirth(signUpRequest.getDateOfBirth())
                .role("USER")
                .build();

        userRepository.save(userEntity);

        String customerId = customerService.createCustomer(userEntity.getId());;
        userEntity.setCustomerId(customerId);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(TokenHelper.generateAccessToken(userEntity))
                .refreshToken(TokenHelper.generateRefreshToken(userEntity))
                .build();

        return UserResponse.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .fullName(userEntity.getFullName())
                .token(tokenResponse)
                .build();
    }

    @Transactional
    public UserResponse logIn(LogInRequest loginRequest) {
        UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail());
        if (Objects.isNull(userEntity)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (!BCrypt.checkpw(loginRequest.getPassword(), userEntity.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(TokenHelper.generateAccessToken(userEntity))
                .refreshToken(TokenHelper.generateRefreshToken(userEntity))
                .build();

        return UserResponse.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .fullName(userEntity.getFullName())
                .token(tokenResponse)
                .build();
    }

    @Transactional
    public TokenResponse refreshToken(RefreshRequest refreshRequest) {
        if (Boolean.FALSE.equals(TokenHelper.validateRefreshToken(refreshRequest.getRefreshToken()))) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Long userId = TokenHelper.getUserIdFromToken("Bearer " + refreshRequest.getRefreshToken());

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        return TokenResponse.builder()
                .accessToken(TokenHelper.generateAccessToken(userEntity))
                .refreshToken(TokenHelper.generateRefreshToken(userEntity))
                .build();

    }

    @Transactional
    public UserResponse loginWithBiometric(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(TokenHelper.generateAccessToken(userEntity))
                .refreshToken(TokenHelper.generateRefreshToken(userEntity))
                .build();

        return UserResponse.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .fullName(userEntity.getFullName())
                .token(tokenResponse)
                .build();
    }

    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest, Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        if (!BCrypt.checkpw(changePasswordRequest.getOldPassword(), userEntity.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        userEntity.setPassword(BCrypt.hashpw(changePasswordRequest.getNewPassword(), BCrypt.gensalt()));
        userRepository.save(userEntity);
    }

    @Transactional
    public void sendOTP(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (Objects.isNull(userEntity)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        String OTP = generateCode();

        presenceService.plusCode(email, OTP);

        eventPublisher.publishEvent(
                new OTPEvent(
                        email,
                        userEntity.getFullName(),
                        OTP
                )
        );

    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        UserEntity userEntity = userRepository.findByEmail(forgotPasswordRequest.getEmail());

        if (Objects.isNull(userEntity)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (!forgotPasswordRequest.getOtp().equals(presenceService.getCode(forgotPasswordRequest.getEmail()))) {
            throw new AppException(ErrorCode.OTP_NOT_MATCH);
        }

        if(!forgotPasswordRequest.getNewPassword().equals(forgotPasswordRequest.getConfirmNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        userEntity.setPassword(BCrypt.hashpw(forgotPasswordRequest.getNewPassword(), BCrypt.gensalt()));
        userRepository.save(userEntity);
        presenceService.delete(forgotPasswordRequest.getEmail());
    }

    private String generateCode() {
        String uuid = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        return uuid.substring(0, 6);
    }
}
