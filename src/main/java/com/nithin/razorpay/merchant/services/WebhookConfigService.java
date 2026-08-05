package com.nithin.razorpay.merchant.services;

import com.nithin.razorpay.merchant.dto.request.UpdateWebhookConfigRequest;
import com.nithin.razorpay.merchant.dto.response.WebhookConfigResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


public interface WebhookConfigService {

    WebhookConfigResponse create(UUID merchantId, UpdateWebhookConfigRequest request);

    List<WebhookConfigResponse> list(UUID merchantId);

    WebhookConfigResponse getById(UUID merchantId,UUID configId);

    WebhookConfigResponse update(UUID merchantId,UUID configId, UpdateWebhookConfigRequest request);

    void delete(UUID merchantId, UUID configId);


}
