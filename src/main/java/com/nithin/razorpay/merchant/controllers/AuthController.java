package com.nithin.razorpay.merchant.controllers;

import com.nithin.razorpay.merchant.dto.request.LoginRequest;
import com.nithin.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.nithin.razorpay.merchant.dto.response.LoginResponse;
import com.nithin.razorpay.merchant.dto.response.MerchantResponse;
import com.nithin.razorpay.merchant.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MerchantResponse> signup(@RequestBody @Valid MerchantSignupRequest merchantSignupRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                authService.create(merchantSignupRequest)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
