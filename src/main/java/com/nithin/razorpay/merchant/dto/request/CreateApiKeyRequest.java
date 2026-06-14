package com.nithin.razorpay.merchant.dto.request;

import com.nithin.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
