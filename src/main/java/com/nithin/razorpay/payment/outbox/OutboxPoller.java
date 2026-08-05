package com.nithin.razorpay.payment.outbox;

import com.nithin.razorpay.common.config.KafkaProperties;
import com.nithin.razorpay.common.enums.OutboxStatus;
import com.nithin.razorpay.payment.entities.OutboxEvent;
import com.nithin.razorpay.payment.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String , Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final OutboxResultHandler outboxResultHandler;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void poll(){
        List<OutboxEvent> events = outboxEventRepository.findByStatusOrderByCreatedAt(OutboxStatus.PENDING);

        for(OutboxEvent event:events){
            try{
                String topic = kafkaProperties.topicFor(event.getAggregateType());
                String key = extractMerchantId(event.getPayload());

                Map<String , Object> envelope = Map.of(
                        "eventType",event.getEventType(),
                        "aggregateType",event.getAggregateType(),
                        "aggregateId",event.getAggregateId(),
                        "data",event.getPayload()

                );


                kafkaTemplate.send(topic,key,envelope).get(5, TimeUnit.SECONDS);

                outboxResultHandler.handleEventPublished(event);

            }
            catch(Exception e){
                log.error("Outbox event failed , eventId : {} ,attempts : {}",event.getId(),event.getAttempts());
                outboxResultHandler.handleEventFailed(event,e.getMessage());
            }
        }
    }

    private String extractMerchantId(Map<String, Object> payload) {
        return payload.get("merchantId") != null ? payload.get("merchantId").toString() : "unknown";
    }
}
