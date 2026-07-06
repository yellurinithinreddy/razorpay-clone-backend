package com.nithin.razorpay.vault.dto.response;

import com.nithin.razorpay.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear

) {
}
