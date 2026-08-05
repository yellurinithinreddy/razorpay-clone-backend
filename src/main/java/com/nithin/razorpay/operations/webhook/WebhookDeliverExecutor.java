package com.nithin.razorpay.operations.webhook;

import com.nithin.razorpay.common.enums.WebhookEventStatus;
import com.nithin.razorpay.operations.entities.WebhookEvent;
import com.nithin.razorpay.operations.repositories.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebhookDeliverExecutor {

    private final WebhookEventRepository webhookEventRepository;
    private final RestClient restClient;
    private final WebhookRetryQueue webhookRetryQueue;

    private final List<Duration> BACKOFF = List.of(
            Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofMinutes(30),
            Duration.ofHours(2), Duration.ofHours(8), Duration.ofHours(24)
    );

    private final int MAX_ATTEMPTS = 7;

    @Value("${webhook.delivery.signtaure-header:X-Razorpay-Signature}")
    private String signatureHeader;

    @Transactional
    public void deliver(UUID webhookEventId){

        Optional<WebhookEvent> webhookEvent = webhookEventRepository.findById(webhookEventId);

        if(webhookEvent.isEmpty()){
            log.warn("No webhook event found for this id: {}",webhookEventId);
            return ;
        }

        WebhookEvent event = webhookEvent.get();


        if(event.getStatus() == WebhookEventStatus.DELIVERED || event.getStatus() == WebhookEventStatus.DEAD){
            log.warn("Cannot deliver the event {} in status: {}",webhookEventId,event.getStatus());
            return ;
        }

        event.setAttempts(event.getAttempts()+1);
        event.setLastAttemptAt(LocalDateTime.now());
        try{

            var response = restClient.post()
                    .uri(event.getTargetUrl())
                    .header(signatureHeader, event.getSignature())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("event",event.getEventType(), "payload",event.getPayload()))
                    .retrieve()
                    .toBodilessEntity();

            int statusCode = response.getStatusCode().value();
            event.setLastResponseCode(statusCode);

            if(response.getStatusCode().is2xxSuccessful()){
                event.setDeliveredAt(LocalDateTime.now());
                event.setStatus(WebhookEventStatus.DELIVERED);
                webhookEventRepository.save(event);
                log.info("Webhook event of id {} and event type {} is delivered ",webhookEventId,event.getEventType());
                return ;
            }

            handleEventFailed(event,"HTTP"+response.getStatusCode());
        } catch(RestClientException e){
            log.error("Webhook event of id {} and event type {} is Failed", webhookEventId, event.getEventType());
            handleEventFailed(event,e.getMessage());

        }




    }

    private void handleEventFailed(WebhookEvent event, String errorMessage) {

        if(event.getAttempts() >= MAX_ATTEMPTS){
            event.setStatus(WebhookEventStatus.DEAD);
            // handle dlq
            return ;
        }

        LocalDateTime retryAt = LocalDateTime.now().plus(BACKOFF.get(event.getAttempts()-1));
        event.setNextRetryAt(retryAt);
        event.setLastResponseBody(errorMessage);
        event.setStatus(WebhookEventStatus.FAILED);

        webhookRetryQueue.enqueue(event.getId(), retryAt);



    }
}
