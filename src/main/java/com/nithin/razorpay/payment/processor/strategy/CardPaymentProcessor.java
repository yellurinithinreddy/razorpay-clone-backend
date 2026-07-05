package com.nithin.razorpay.payment.processor.strategy;

import com.nithin.razorpay.payment.processor.PaymentProcessor;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
