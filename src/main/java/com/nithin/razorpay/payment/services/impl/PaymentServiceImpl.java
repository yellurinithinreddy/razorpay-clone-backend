package com.nithin.razorpay.payment.services.impl;

import com.nithin.razorpay.common.enums.OrderStatus;
import com.nithin.razorpay.common.enums.PaymentMethod;
import com.nithin.razorpay.common.enums.PaymentStatus;
import com.nithin.razorpay.common.exceptions.BusinessRuleViolationException;
import com.nithin.razorpay.common.exceptions.ResourceNotFoundException;
import com.nithin.razorpay.payment.dto.request.PaymentInitRequest;
import com.nithin.razorpay.payment.dto.response.PaymentResponse;
import com.nithin.razorpay.payment.entities.OrderRecord;
import com.nithin.razorpay.payment.entities.Payment;
import com.nithin.razorpay.payment.gateway.PaymentGatewayRouter;
import com.nithin.razorpay.payment.gateway.dto.PaymentRequest;
import com.nithin.razorpay.payment.gateway.dto.PaymentResult;
import com.nithin.razorpay.payment.mapper.PaymentMapper;
import com.nithin.razorpay.payment.repositories.OrderRepository;
import com.nithin.razorpay.payment.repositories.PaymentRepository;
import com.nithin.razorpay.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
        OrderRecord order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order",request.orderId()));

        if(order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus() != OrderStatus.ATTEMPTED){
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE","Order cannot accept payment in status: "+order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts()+1);

        Payment payment = Payment.builder()
                .merchantId(merchantId)
                .method(request.method())
                .methodDetails(request.methodDetails())
                .amount(order.getAmount())
                .status(PaymentStatus.CREATED)
                .order(order)
                .build();
        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(payment.getId(),order.getId(),
                merchantId,order.getAmount(),
                request.method(),request.methodDetails());

        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch(result){
            case PaymentResult.Pending pending -> {
                payment.setProcessorReference(pending.registrationRef());
            }
            case PaymentResult.Failure failure -> {
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
//                payment.setFailedAt(LocalDateTime.now());
                payment.setStatus(PaymentStatus.FAILED);
            }
        }
        payment = paymentRepository.save(payment);
        orderRepository.save(order);


        return paymentMapper.toResponse(payment);
    }
}
