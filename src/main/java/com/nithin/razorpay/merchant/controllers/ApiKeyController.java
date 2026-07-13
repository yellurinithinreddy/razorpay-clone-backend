package com.nithin.razorpay.merchant.controllers;

import com.nithin.razorpay.merchant.dto.request.ApiKeyResponse;
import com.nithin.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.nithin.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.nithin.razorpay.merchant.security.MerchantContext;
import com.nithin.razorpay.merchant.services.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/merchants/api-keys")
@Slf4j
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@RequestBody @Valid CreateApiKeyRequest createApiKeyRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.create(merchantContext.getMerchantId(),createApiKeyRequest));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> list(){
        return ResponseEntity.status(HttpStatus.OK).body(apiKeyService.list(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID keyId){
        apiKeyService.revoke(merchantContext.getMerchantId(),keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyResponse> rotate(@PathVariable UUID keyId){
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.rotate(merchantContext.getMerchantId(),keyId));
    }

}
