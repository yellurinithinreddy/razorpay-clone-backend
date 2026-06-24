package com.nithin.razorpay.payment.controllers;

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
    private final UUID merchantId = UUID.fromString("bf8826ef-f715-48fd-bb7f-ed5d3509b107");

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest createOrderRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(merchantId,createOrderRequest));
    }

    @GetMapping("merchants/{merchantId}/order/{orderId}")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID merchantId,@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getById(merchantId,orderId));
    }

    @PostMapping("merchants/{merchantId}/order/{orderId}")
    public ResponseEntity<OrderResponse> cancel(@PathVariable UUID merchantId,@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.cancel(merchantId,orderId));
    }

    @GetMapping("/payments/merchants/{merchantId}/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getAllPaymentsByOrder(@PathVariable UUID merchantId, @PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllPaymentsByOrder(merchantId,orderId));
    }
}
