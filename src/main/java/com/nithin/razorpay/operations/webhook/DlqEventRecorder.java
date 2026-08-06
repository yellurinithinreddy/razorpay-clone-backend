package com.nithin.razorpay.operations.webhook;

import com.nithin.razorpay.common.enums.WebhookEventStatus;
import com.nithin.razorpay.operations.entities.DlqEvent;
import com.nithin.razorpay.operations.entities.WebhookEvent;
import com.nithin.razorpay.operations.repositories.DlqEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DlqEventRecorder {

    private final DlqEventRepository dlqEventRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAfterAttemptsExhausted(WebhookEvent event, String error){
        log.info("Recording the dlq Event with webhook event id {}",event.getId());
        event.setStatus(WebhookEventStatus.DEAD);

        DlqEvent dlqEvent = DlqEvent.builder()
                .webhookEvent(event)
                .merchantId(event.getMerchantId())
                .finalError(error)
                .payload(event.getPayload())
                .build();

        dlqEventRepository.save(dlqEvent);
    }

    public void recordConsumerFailed(ConsumerRecord<String, Map<String, Object>> record, String error) {
        log.debug("Recording the dlq because consumer failed {}",record.toString());
        Map<String, Object> envelope = null;
        UUID merchantId = null;

        try{
            envelope = record.value();
            Map<String, Object> data = (Map<String, Object>) envelope.get("data");
            Object merchantIdRaw = data.get("merchantId");
            if(merchantIdRaw != null){
                merchantId = UUID.fromString(merchantIdRaw.toString());
            }
        } catch(Exception ignored){

        }


        DlqEvent dlqEvent = DlqEvent.builder()
                .merchantId(merchantId)
                .payload(envelope != null ? envelope : Map.of())
                .webhookEvent(null)
                .finalError(error)
                .build();

        dlqEventRepository.save(dlqEvent);
    }
}
