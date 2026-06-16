package com.nithin.razorpay.merchant.controllers;

import com.nithin.razorpay.merchant.dto.request.ApiKeyResponse;
import com.nithin.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.nithin.razorpay.merchant.dto.response.ApiKeyCreateResponse;
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
@RequestMapping("v1/merchants/{merchantId}/api-keys")
@Slf4j
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@PathVariable UUID merchantId, @RequestBody @Valid CreateApiKeyRequest createApiKeyRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.create(merchantId,createApiKeyRequest));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> list(@PathVariable UUID merchantId){
        return ResponseEntity.status(HttpStatus.OK).body(apiKeyService.list(merchantId));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID merchantId,@PathVariable UUID keyId){
        apiKeyService.revoke(merchantId,keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyResponse> rotate(@PathVariable UUID merchantId,@PathVariable UUID keyId){
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.rotate(merchantId,keyId));
    }

}
