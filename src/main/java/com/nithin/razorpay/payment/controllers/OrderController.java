package com.nithin.razorpay.payment.controllers;

import com.nithin.razorpay.merchant.security.MerchantContext;
import com.nithin.razorpay.payment.dto.request.CreateOrderRequest;
import com.nithin.razorpay.payment.dto.response.OrderResponse;
import com.nithin.razorpay.payment.dto.response.PaymentResponse;
import com.nithin.razorpay.payment.services.OrderService;
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
@Slf4j
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest createOrderRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(merchantContext.getMerchantId(),createOrderRequest));
    }

    @GetMapping("merchants/order/{orderId}")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getById(merchantContext.getMerchantId(),orderId));
    }

    @PostMapping("merchants/order/{orderId}")
    public ResponseEntity<OrderResponse> cancel(@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.cancel(merchantContext.getMerchantId(),orderId));
    }

    @GetMapping("/payments/merchants/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getAllPaymentsByOrder(@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllPaymentsByOrder(merchantContext.getMerchantId(),orderId));
    }
}
