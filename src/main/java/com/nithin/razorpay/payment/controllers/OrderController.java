package com.nithin.razorpay.payment.controllers;

import com.nithin.razorpay.payment.dto.request.CreateOrderRequest;
import com.nithin.razorpay.payment.dto.response.OrderResponse;
import com.nithin.razorpay.payment.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
