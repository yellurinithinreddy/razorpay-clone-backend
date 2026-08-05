package com.nithin.razorpay.payment.outbox;

import com.nithin.razorpay.common.enums.OutboxStatus;
import com.nithin.razorpay.payment.entities.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxResultHandler {

    private final Integer MAX_ATTEMPTS = 3;

    @Transactional
    public void handleEventPublished(OutboxEvent event){
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
    }

    @Transactional
    public void handleEventFailed(OutboxEvent event,String lastError){
        event.setAttempts(event.getAttempts()+1);
        event.setLastError(
                lastError.length() < 1000 ? lastError : lastError.substring(0,1000)
        );
        if(event.getAttempts() >= MAX_ATTEMPTS){
            event.setStatus(OutboxStatus.FAILED);
        }

    }
}
