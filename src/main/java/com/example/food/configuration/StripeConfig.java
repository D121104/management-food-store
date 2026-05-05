package com.example.food.configuration;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "stripe")
@AllArgsConstructor
@Getter
public class StripeConfig {
    private final String secretKey;
    private final String webhookSecret;

    @PostConstruct
    public void initStripe() {
        if (secretKey != null) {
            Stripe.apiKey = secretKey;
        } else {
            throw new IllegalArgumentException("secretKey is null");
        }
    }
}
