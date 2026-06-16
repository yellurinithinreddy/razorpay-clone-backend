package com.nithin.razorpay.merchant.services;

import com.nithin.razorpay.merchant.dto.request.ApiKeyResponse;
import com.nithin.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.nithin.razorpay.merchant.dto.response.ApiKeyCreateResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {

    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest);

    List<ApiKeyResponse> list(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    ApiKeyResponse rotate(UUID merchantId, UUID keyId);
}
