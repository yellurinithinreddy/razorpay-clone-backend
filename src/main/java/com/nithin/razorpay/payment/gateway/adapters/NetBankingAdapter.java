package com.nithin.razorpay.payment.gateway.adapters;

import com.nithin.razorpay.payment.gateway.PaymentAdapter;
import com.nithin.razorpay.payment.gateway.dto.PaymentRequest;
import com.nithin.razorpay.payment.gateway.dto.PaymentResult;

public class NetBankingAdapter implements PaymentAdapter {
    @Override
    public PaymentResult initiate(PaymentRequest request) {
        return null;
    }
}
