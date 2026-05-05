package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.dto.request.payment.CreatePaymentIntentRequest;
import com.example.food.service.PaymentService;
import com.stripe.model.PaymentIntent;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create-intent")
    public ApiResponse<String> createPaymentIntent(@RequestBody CreatePaymentIntentRequest createPaymentIntentRequest) {
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(createPaymentIntentRequest);

        return ApiResponse.<String>builder()
                .code(200)
                .message("Tạo payment intent thành công")
                .result(paymentIntent.getClientSecret())
                .build();
    }
}
