package com.example.food.dto.request.payment_method;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreatePaymentMethodRequest {
    private String paymentMethodId;
}
