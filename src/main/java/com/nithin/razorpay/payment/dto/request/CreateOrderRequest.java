package com.nithin.razorpay.payment.dto.request;

import com.nithin.razorpay.common.entities.Money;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(
        @NotNull(message = "amount is required")
        Money amount,

        @Size(max = 100)
        String receipt,

        Map<String,Object> notes,

        LocalDateTime expiresAt
) {
}
