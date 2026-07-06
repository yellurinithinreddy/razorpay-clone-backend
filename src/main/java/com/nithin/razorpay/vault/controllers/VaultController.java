package com.nithin.razorpay.vault.controllers;

import com.nithin.razorpay.vault.dto.request.TokenizeRequest;
import com.nithin.razorpay.vault.dto.response.TokenizeResponse;
import com.nithin.razorpay.vault.services.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vault")
public class VaultController {

    private final VaultService vaultService;
    private final UUID merchantId = UUID.fromString("bf8826ef-f715-48fd-bb7f-ed5d3509b107");

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(@RequestBody TokenizeRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(vaultService.tokenize(request,merchantId));
    }
}
