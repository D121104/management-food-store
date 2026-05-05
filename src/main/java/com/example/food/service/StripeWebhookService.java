package com.example.food.service;


import com.example.food.common.PaymentStatus;
import com.example.food.configuration.StripeConfig;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class StripeWebhookService {

    private final StripeConfig stripeConfig;
    private final OrderService orderService;

    public void handleWebhook(String payload, String sigHeader) {
        final Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            throw new AppException(ErrorCode.INVALID_WEBHOOK_SIGNATURE);
        }
        if (!(event.getDataObjectDeserializer().getObject().orElse(null) instanceof PaymentIntent paymentIntent)) {
            return;
        }
        String orderIdStr = paymentIntent.getMetadata().get("order_id");
        if (orderIdStr == null) {
            throw new AppException(ErrorCode.HANDLE_WEBHOOK_FAILED);
        }
        Long orderId = Long.parseLong(orderIdStr);
        switch (event.getType()) {
            case "payment_intent.succeeded":
                orderService.updateOrderPaymentStatus(orderId, PaymentStatus.PAID);
                break;
            case "payment_intent.payment_failed": // fixed
                orderService.updateOrderPaymentStatus(orderId, PaymentStatus.FAILED);
                break;
            case "payment_intent.canceled": // fixed
                orderService.updateOrderPaymentStatus(orderId, PaymentStatus.CANCELED);
                break;
            case "payment_intent.processing":
                orderService.updateOrderPaymentStatus(orderId, PaymentStatus.PROCESSING);
                break;
            case "payment_intent.created":
                // optional: keep pending status
                break;
            default:
                // Don't throw for unknown Stripe events
                break;
        }
    }


}
