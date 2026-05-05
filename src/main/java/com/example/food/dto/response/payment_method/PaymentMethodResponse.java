package com.example.food.dto.response.payment_method;

import com.example.food.common.CardBrand;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PaymentMethodResponse {
    private String id;
    private Long userId;
    private String holderName;
    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;

    private String last4;
    private String exp_month;
    private String exp_year;
}
