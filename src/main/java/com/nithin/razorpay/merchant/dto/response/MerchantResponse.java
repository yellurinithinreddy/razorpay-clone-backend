package com.nithin.razorpay.merchant.dto.response;

import com.nithin.razorpay.common.enums.BusinessType;
import com.nithin.razorpay.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
}
