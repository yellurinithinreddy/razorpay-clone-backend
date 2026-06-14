package com.nithin.razorpay.merchant.dto.response;

import com.nithin.razorpay.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String rawSecret,
        Environment environment
) {
}
