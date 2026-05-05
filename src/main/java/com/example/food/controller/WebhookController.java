package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.service.StripeWebhookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/webhooks")
public class WebhookController {
    private final StripeWebhookService stripeWebhookService;

    @PostMapping("/stripe")
    public ApiResponse<String> handleStripWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        stripeWebhookService.handleWebhook(payload, sigHeader);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Xử lý webhook thành công")
                .build();
    }
}
