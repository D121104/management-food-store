package com.example.food.dto.request.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreatePaymentIntentRequest {
    private Long orderId;
    private Long userId;
    private String paymentMethodId;
}
