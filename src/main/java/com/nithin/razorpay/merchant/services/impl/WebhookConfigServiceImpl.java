package com.nithin.razorpay.merchant.services.impl;

import com.nithin.razorpay.common.exceptions.ResourceNotFoundException;
import com.nithin.razorpay.common.util.RandomizerUtil;
import com.nithin.razorpay.merchant.api.MerchantWebhookApi;
import com.nithin.razorpay.merchant.dto.request.UpdateWebhookConfigRequest;
import com.nithin.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.nithin.razorpay.common.dto.WebhookTarget;
import com.nithin.razorpay.merchant.entities.Merchant;
import com.nithin.razorpay.merchant.entities.MerchantWebhookConfig;
import com.nithin.razorpay.merchant.mapper.MerchantWebhookConfigMapper;
import com.nithin.razorpay.merchant.repositories.MerchantRepository;
import com.nithin.razorpay.merchant.repositories.MerchantWebhookConfigRepository;
import com.nithin.razorpay.merchant.services.WebhookConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookConfigServiceImpl implements WebhookConfigService, MerchantWebhookApi {

    private final BytesEncryptor bytesEncryptor;
    private final MerchantRepository merchantRepository;
    private final MerchantWebhookConfigRepository merchantWebhookConfigRepository;
    private final MerchantWebhookConfigMapper merchantWebhookConfigMapper;

    @Override
    public WebhookConfigResponse create(UUID merchantId, UpdateWebhookConfigRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant",merchantId));
        String rawSecret = RandomizerUtil.randomBase64(32);
        byte[] rawSecretBytes = Base64.getDecoder().decode(rawSecret);
        String encryptedSecret = Base64.getEncoder().encodeToString(bytesEncryptor.encrypt(rawSecretBytes));

        MerchantWebhookConfig merchantWebhookConfig = MerchantWebhookConfig.builder()
                .merchant(merchant)
                .targetUrl(request.targetUrl())
                .enabled(true)
                .eventTypes(request.eventTypes())
                .webhookSecret(encryptedSecret)
                .build();

        merchantWebhookConfig = merchantWebhookConfigRepository.save(merchantWebhookConfig);
        return merchantWebhookConfigMapper.toResponse(merchantWebhookConfig,rawSecret);
    }

    @Override
    public List<WebhookConfigResponse> list(UUID merchantId) {
        return merchantWebhookConfigRepository.findByMerchant_Id(merchantId)
                .stream()
                .map(config -> merchantWebhookConfigMapper.toResponse(config,null))
                .toList();
    }

    @Override
    public WebhookConfigResponse getById(UUID merchantId, UUID configId) {
        MerchantWebhookConfig merchantWebhookConfig = requireOwnedConfig(merchantId,configId);
         return merchantWebhookConfigMapper.toResponse(merchantWebhookConfig,null);
    }

    @Override
    @Transactional
    public WebhookConfigResponse update(UUID merchantId, UUID configId, UpdateWebhookConfigRequest request) {
        MerchantWebhookConfig merchantWebhookConfig = requireOwnedConfig(merchantId,configId);
        merchantWebhookConfig.setEventTypes(request.eventTypes());
        merchantWebhookConfig.setTargetUrl(request.targetUrl());
        log.info("Merchant webhook config updated id={} merchantId={}",configId,merchantId);
        return merchantWebhookConfigMapper.toResponse(merchantWebhookConfig,null);
    }

    @Override
    public void delete(UUID merchantId, UUID configId) {
        MerchantWebhookConfig merchantWebhookConfig = requireOwnedConfig(merchantId,configId);
        merchantWebhookConfigRepository.delete(merchantWebhookConfig);
        log.info("Merchant webhook config deleted id={} merchantId={}",configId,merchantId);
    }

    private MerchantWebhookConfig requireOwnedConfig(UUID merchantId,UUID configId){
        return merchantWebhookConfigRepository.findByIdAndMerchant_Id(configId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("MerchantWebhookConfig",configId));
    }

    @Override
    public List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType) {
        return merchantWebhookConfigRepository.findByMerchant_IdAndEnabledTrue(merchantId)
                .stream()
                .filter(config -> config.isSubscribedTo(eventType))
                .map(config -> {
                    byte[] secretBytes = Base64.getDecoder().decode(config.getWebhookSecret());
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(secretBytes);
                    return new WebhookTarget(config.getId(),config.getTargetUrl(),new String(decryptedSecretBytes,StandardCharsets.UTF_8));
                })
                .toList();
    }
}
