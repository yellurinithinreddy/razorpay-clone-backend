package com.nithin.razorpay.operations.webhook;

import com.nithin.razorpay.common.dto.WebhookTarget;
import com.nithin.razorpay.common.enums.WebhookEventStatus;
import com.nithin.razorpay.common.util.SignatureUtil;
import com.nithin.razorpay.merchant.api.MerchantWebhookApi;
import com.nithin.razorpay.operations.entities.WebhookEvent;
import com.nithin.razorpay.operations.repositories.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookKafkaConsumer {


    private final MerchantWebhookApi merchantWebhookApi;
    private final WebhookEventRepository webhookEventRepository;
    private final ObjectMapper objectMapper;
    private final SignatureUtil signatureUtil;
    private final WebhookRetryQueue webhookRetryQueue;

    @KafkaListener(topics = {
          "${app.kafka.topics.payment:payment.events}",
            "${app.kafka.topics.order:order.events}",
            "${app.kafka.topics.refund:refund.events}",
            "${app.kafka.topics.settlement:settlement.events}"
    })
    public void onWebhookEvent(ConsumerRecord<String, Map<String , Object>> record, Acknowledgment acknowledgment) {

        try{

            Map<String,Object> envelope = record.value();
            Map<String,Object> data = (Map<String, Object>) envelope.get("data");
            String eventType = envelope.get("eventType").toString();
            Object merchantIdRaw = data.get("merchantId");

            if(merchantIdRaw == null){
                log.warn("No merchantId was found , skipping event: {}", eventType);
                acknowledgment.acknowledge();
                return ;
            }

            UUID merchantId = UUID.fromString(merchantIdRaw.toString());
            List<WebhookTarget> targets = merchantWebhookApi.getActiveConfigsForEvent(merchantId,eventType);

            Map<String, Object> signatureData = Map.of("eventType",eventType,"payload",data);
            String signatureJson = objectMapper.writeValueAsString(signatureData);

            for(WebhookTarget target: targets){
                String signature = signatureUtil.sign(signatureJson,target.webhookSecret());

                WebhookEvent webhookEvent = WebhookEvent.builder()
                        .merchantId(merchantId)
                        .eventType(eventType)
                        .nextRetryAt(LocalDateTime.now())
                        .payload(data)
                        .signature(signature)
                        .targetUrl(target.targetUrl())
                        .status(WebhookEventStatus.PENDING)
                        .build();

                webhookEvent = webhookEventRepository.save(webhookEvent);

                webhookRetryQueue.enqueue(webhookEvent.getId(), LocalDateTime.now());

            }
        }catch(Exception e){
            log.error("webhook consumer failed to process the record , offset: {}",record.offset());
//            TODO: handle exception while ack and retry
        }

    }
}
