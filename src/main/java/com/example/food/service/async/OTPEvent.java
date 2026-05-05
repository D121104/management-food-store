package com.example.food.service.async;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OTPEvent {

    private final String email;
    private final String fullName;
    private final String otp;
}
