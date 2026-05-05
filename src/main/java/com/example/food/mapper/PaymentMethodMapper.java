package com.example.food.mapper;

import com.example.food.dto.response.payment_method.PaymentMethodResponse;
import com.example.food.entity.PaymentMethodEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentMethodMapper {
    PaymentMethodResponse toPaymentMethodResponse(PaymentMethodEntity paymentMethodEntity);
}
