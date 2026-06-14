package com.nithin.razorpay.merchant.services;

import com.nithin.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.nithin.razorpay.merchant.dto.response.ApiKeyCreateResponse;

import java.util.UUID;

public interface ApiKeyService {

    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest);
}
