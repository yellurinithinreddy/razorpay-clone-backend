package com.nithin.razorpay.payment.services.impl;

import com.nithin.razorpay.common.enums.OrderStatus;
import com.nithin.razorpay.common.exceptions.DuplicateResourceException;
import com.nithin.razorpay.payment.dto.request.CreateOrderRequest;
import com.nithin.razorpay.payment.dto.response.OrderResponse;
import com.nithin.razorpay.payment.entities.OrderRecord;
import com.nithin.razorpay.payment.repositories.OrderRepository;
import com.nithin.razorpay.payment.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId,CreateOrderRequest createOrderRequest) {
        if(createOrderRequest.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId,createOrderRequest.receipt())){
            throw new DuplicateResourceException("RDER_RECEIPT_DUPLICATE","Order with receipt already exists: "+createOrderRequest.receipt());
        }

        OrderRecord order = OrderRecord.builder()
                .amount(createOrderRequest.amount())
                .expiresAt(createOrderRequest.expiresAt() == null ? LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes) : createOrderRequest.expiresAt())
                .receipt(createOrderRequest.receipt())
                .notes(createOrderRequest.notes())
                .orderStatus(OrderStatus.CREATED)
                .merchantId(merchantId)
                .build();

        order = orderRepository.save(order);

        // TODO: send kafka event that order is created.

        return new OrderResponse(order.getId()
                ,order.getMerchantId()
                ,order.getReceipt()
                ,order.getAmount()
                ,order.getOrderStatus()
                ,order.getAttempts()
                ,order.getNotes()
                ,order.getExpiresAt()
                , null);
    }
}
