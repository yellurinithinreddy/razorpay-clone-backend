package com.nithin.razorpay.payment.services.impl;

import com.nithin.razorpay.common.enums.OrderStatus;
import com.nithin.razorpay.common.exceptions.BusinessRuleViolationException;
import com.nithin.razorpay.common.exceptions.DuplicateResourceException;
import com.nithin.razorpay.common.exceptions.ResourceNotFoundException;
import com.nithin.razorpay.merchant.services.CustomerService;
import com.nithin.razorpay.payment.dto.request.CreateOrderRequest;
import com.nithin.razorpay.payment.dto.response.OrderResponse;
import com.nithin.razorpay.payment.dto.response.PaymentResponse;
import com.nithin.razorpay.payment.entities.OrderRecord;
import com.nithin.razorpay.payment.entities.Payment;
import com.nithin.razorpay.payment.mapper.OrderMapper;
import com.nithin.razorpay.payment.mapper.PaymentMapper;
import com.nithin.razorpay.payment.repositories.OrderRepository;
import com.nithin.razorpay.payment.repositories.PaymentRepository;
import com.nithin.razorpay.payment.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final CustomerService customerService;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId,CreateOrderRequest createOrderRequest) {
        if(createOrderRequest.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId,createOrderRequest.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE","Order with receipt already exists: "+createOrderRequest.receipt());
        }

        UUID customer = null;

        if(createOrderRequest.customer() != null){
            customer = customerService.findOrCreate(merchantId,
                    createOrderRequest.customer().email(),createOrderRequest.customer().name(),
                    createOrderRequest.customer().phone());
        }

        OrderRecord order = OrderRecord.builder()
                .amount(createOrderRequest.amount())
                .expiresAt(createOrderRequest.expiresAt() == null ? LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes) : createOrderRequest.expiresAt())
                .receipt(createOrderRequest.receipt())
                .customerId(customer)
                .notes(createOrderRequest.notes())
                .orderStatus(OrderStatus.CREATED)
                .merchantId(merchantId)
                .build();


        order = orderRepository.save(order);

        // TODO: send kafka event that order is created.

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER",orderId));

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER",orderId));

        if(order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.PAID){
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL","Cannot cancel order with status: "+order.getOrderStatus());
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Override
    public List<PaymentResponse> getAllPaymentsByOrder(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER",orderId));

        List<Payment> payments = paymentRepository.findByOrder_Id(orderId);

//        return payments.stream()
//                .map(payment -> paymentMapper.toResponse(payment))
//                .toList();
        return paymentMapper.toResponseList(payments);

    }


}
