package com.nithin.razorpay.payment.gateway;

import com.nithin.razorpay.payment.gateway.dto.PaymentRequest;
import com.nithin.razorpay.payment.gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {

    PaymentResult initiate(PaymentRequest request);

    PaymentResult capture(UUID paymentId);
}
