package com.nithin.razorpay.payment.services;

import com.nithin.razorpay.payment.dto.request.PaymentInitRequest;
import com.nithin.razorpay.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId,PaymentInitRequest request);

    PaymentResponse capture(UUID merchantId,UUID paymentId);
}
