package com.nithin.razorpay.vault.services;

import com.nithin.razorpay.common.entities.Money;
import com.nithin.razorpay.common.enums.PaymentMethod;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.nithin.razorpay.vault.dto.request.TokenizeRequest;
import com.nithin.razorpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {

    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID uuid, String token, Money amount, Map<String,Object> methodDetails);
}
