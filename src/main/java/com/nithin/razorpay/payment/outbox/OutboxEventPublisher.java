package com.nithin.razorpay.payment.outbox;

import com.nithin.razorpay.common.enums.EventAggregateType;
import com.nithin.razorpay.payment.entities.OutboxEvent;
import com.nithin.razorpay.payment.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;

    public void publish(EventAggregateType aggregateType, UUID aggregateId, String eventType, Map<String,Object> payload) {
        OutboxEvent event = OutboxEvent.builder()
                .eventType(eventType)
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .payload(payload)
                .build();

        outboxEventRepository.save(event);
    }
}
