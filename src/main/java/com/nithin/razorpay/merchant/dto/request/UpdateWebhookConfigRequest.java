package com.nithin.razorpay.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateWebhookConfigRequest(
        @NotBlank(message = "Webhook URL is required")
        @Size(max = 500)
        @Pattern(regexp = "^https?://.+",message = "Webhook URL must be valid http(s) URL")
        String targetUrl,

//        comma separated fine-grained event type names (e.g. "PAYMENT_STATUS_CHANGED, REFUND_CREATED").
//        Null/Blank/All subscribes to every event type
        @Size(max = 1000)
        String eventTypes
) {
}
