package com.nithin.razorpay.payment.dto.request;

import com.nithin.razorpay.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PaymentInitRequest(
        @NotNull(message = "OrderId is required")
        UUID orderId,

        @NotNull(message = "Payment Method is required")
        PaymentMethod method,

        Map<String,Object> methodDetails
) {
}
