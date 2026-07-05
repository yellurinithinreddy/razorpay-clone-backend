package com.nithin.razorpay.payment.controllers;

import com.nithin.razorpay.payment.dto.request.PaymentInitRequest;
import com.nithin.razorpay.payment.dto.response.PaymentResponse;
import com.nithin.razorpay.payment.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UUID merchantId = UUID.fromString("bf8826ef-f715-48fd-bb7f-ed5d3509b107");

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@RequestBody @Valid PaymentInitRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiate(merchantId,request));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> capture(@PathVariable UUID paymentId){
        return ResponseEntity.ok(paymentService.capture(merchantId,paymentId));
    }
}
