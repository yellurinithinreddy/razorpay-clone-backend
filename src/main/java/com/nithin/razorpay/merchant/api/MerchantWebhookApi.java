package com.nithin.razorpay.merchant.api;

import com.nithin.razorpay.common.dto.WebhookTarget;

import java.util.List;
import java.util.UUID;

public interface MerchantWebhookApi {

    List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType);
}
