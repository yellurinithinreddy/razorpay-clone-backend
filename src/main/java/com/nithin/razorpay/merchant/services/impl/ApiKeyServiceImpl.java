package com.nithin.razorpay.merchant.services.impl;

import com.nithin.razorpay.common.exceptions.ResourceNotFoundException;
import com.nithin.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.nithin.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.nithin.razorpay.merchant.entities.ApiKey;
import com.nithin.razorpay.merchant.entities.Merchant;
import com.nithin.razorpay.merchant.repositories.ApiKeyRepository;
import com.nithin.razorpay.merchant.repositories.MerchantRepository;
import com.nithin.razorpay.merchant.services.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant",merchantId));

        String keyId = "rzp_"+createApiKeyRequest.environment().name().toUpperCase()+"big_random_string";
        String rawSecret = "big_random_secret";

        ApiKey apiKey = ApiKey.builder()
                .keyId(keyId)
                .keySecretHash(rawSecret)
                .environment(createApiKeyRequest.environment())
                .merchant(merchant)
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret , apiKey.getEnvironment());
    }
}
