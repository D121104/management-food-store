package com.example.food.service.async;

import com.example.food.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OTPEventListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleScoreEvent(OTPEvent event) {

        emailService.sendScoreEmail(
                event.getEmail(),
                event.getFullName(),
                event.getOtp()
        );

    }
}
