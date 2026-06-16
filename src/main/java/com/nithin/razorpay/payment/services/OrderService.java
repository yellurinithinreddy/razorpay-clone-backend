package com.nithin.razorpay.payment.services;

import com.nithin.razorpay.payment.dto.request.CreateOrderRequest;
import com.nithin.razorpay.payment.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {

    OrderResponse create(UUID merchantId,CreateOrderRequest createOrderRequest);
}
