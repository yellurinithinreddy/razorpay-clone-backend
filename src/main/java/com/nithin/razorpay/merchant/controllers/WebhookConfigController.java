package com.nithin.razorpay.merchant.controllers;

import com.nithin.razorpay.merchant.dto.request.UpdateWebhookConfigRequest;
import com.nithin.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.nithin.razorpay.merchant.security.MerchantContext;
import com.nithin.razorpay.merchant.services.WebhookConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchants/webhooks")
@RequiredArgsConstructor
public class WebhookConfigController {

    private final MerchantContext merchantContext;
    private final WebhookConfigService webhookConfigService;

    @PostMapping
    public ResponseEntity<WebhookConfigResponse> create(@Valid @RequestBody UpdateWebhookConfigRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(webhookConfigService.create(merchantContext.getMerchantId(),request));
    }

    @GetMapping
    public ResponseEntity<List<WebhookConfigResponse>> list(){
        return ResponseEntity.status(HttpStatus.OK).body(webhookConfigService.list(merchantContext.getMerchantId()));
    }

    @GetMapping("/{configId}")
    public ResponseEntity<WebhookConfigResponse> getById(@PathVariable UUID configId){
        return ResponseEntity.status(HttpStatus.OK).body(webhookConfigService.getById(merchantContext.getMerchantId(),configId));
    }

    @PutMapping("/{configId}")
    public ResponseEntity<WebhookConfigResponse> update(@Valid @PathVariable UUID configId,@RequestBody UpdateWebhookConfigRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(webhookConfigService.update(merchantContext.getMerchantId(),configId,request));
    }

    @DeleteMapping("/{configId}")
    public ResponseEntity<Void> delete(@PathVariable UUID configId){
        webhookConfigService.delete(merchantContext.getMerchantId(),configId);
        return ResponseEntity.noContent().build();
    }

}
