package com.nithin.razorpay.payment.processor.dto;

import com.nithin.razorpay.common.entities.Money;
import com.nithin.razorpay.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod method,
        Money amount,
        Map<String,Object> methodDetails
) {
}
