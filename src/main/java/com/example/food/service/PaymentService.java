package com.example.food.service;

import com.example.food.common.Currency;
import com.example.food.common.PaymentStatus;
import com.example.food.dto.request.payment.CreatePaymentIntentRequest;
import com.example.food.entity.OrderEntity;
import com.example.food.entity.UserEntity;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.repository.OrderRepository;
import com.example.food.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class PaymentService {
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentIntent createPaymentIntent(CreatePaymentIntentRequest createPaymentIntentRequest) {
        UserEntity user = userRepository.findById(createPaymentIntentRequest.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        OrderEntity order = orderRepository.findById(createPaymentIntentRequest.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!user.getId().equals(order.getUserId())) {
            throw new AppException(ErrorCode.ORDER_CANT_BE_PAID);
        }

        if (order.getPaymentStatus().equals(PaymentStatus.PAID)) {
            throw new AppException(ErrorCode.ORDER_HAS_BEEN_PAID);
        }

        long amountInCents = order.getTotalPrice() * 100L; // nếu totalPrice trong DB là đơn vị USD
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .putMetadata("order_id", order.getId().toString())
                .setCustomer(user.getCustomerId())
                .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.create(params);
        } catch (StripeException e) {
            throw new AppException(ErrorCode.CREATE_PAYMENT_INTENT_FAILED);
        }

        return paymentIntent;

    }
}
