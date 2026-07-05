package com.nithin.razorpay.payment.processor.dto;

import com.nithin.razorpay.common.entities.Money;
import com.nithin.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID processorReference,
        UUID paymentId,
        PaymentMethod method,
        Money amount,
        String pan,
        String expiry,
        Map<String,Object> methodDetails
) {

    public static PaymentProcessorRequest card(String pan, String expiry, UUID paymentId, PaymentMethod method,Money amount, Map<String,Object> methodDetails){
        return new PaymentProcessorRequest(UUID.randomUUID(),paymentId,method,amount,pan,expiry,methodDetails);
    }

    public static PaymentProcessorRequest nonCard(UUID paymentId, PaymentMethod method, Money amount, Map<String,Object> methodDetails){
        return new PaymentProcessorRequest(UUID.randomUUID(),paymentId,method,amount,null,null,methodDetails);
    }
}
