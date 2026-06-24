package com.nithin.razorpay.merchant.services.impl;

import com.nithin.razorpay.common.exceptions.ResourceNotFoundException;
import com.nithin.razorpay.common.util.RandomizerUtil;
import com.nithin.razorpay.merchant.dto.request.ApiKeyResponse;
import com.nithin.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.nithin.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.nithin.razorpay.merchant.entities.ApiKey;
import com.nithin.razorpay.merchant.entities.Merchant;
import com.nithin.razorpay.merchant.mapper.ApiKeyMapper;
import com.nithin.razorpay.merchant.repositories.ApiKeyRepository;
import com.nithin.razorpay.merchant.repositories.MerchantRepository;
import com.nithin.razorpay.merchant.services.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant",merchantId));

        String keyId = "rzp_"+createApiKeyRequest.environment().name().toLowerCase()+"_"+RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .keyId(keyId)
                .keySecretHash(rawSecret)
                .environment(createApiKeyRequest.environment())
                .merchant(merchant)
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret , apiKey.getEnvironment());
    }

    @Override
    public List<ApiKeyResponse> list(UUID merchantId) {
        return apiKeyMapper.toResponseList(apiKeyRepository.findByMerchant_Id(merchantId));
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey",keyId));

        apiKey.setEnabled(false);

    }

    @Override
    @Transactional
    public ApiKeyResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey",keyId));

        String newRawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(newRawSecret);
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));

        return new ApiKeyResponse(apiKey.getId(), apiKey.getKeyId(), apiKey.getEnvironment(), apiKey.isEnabled(),apiKey.getLastUsedAt(),null);

    }
}
