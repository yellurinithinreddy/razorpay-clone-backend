package com.nithin.razorpay.payment.services.impl;

import com.nithin.razorpay.common.enums.OrderStatus;
import com.nithin.razorpay.common.enums.PaymentEvent;
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
import com.nithin.razorpay.payment.statemachine.PaymentTransitionService;
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

    private final PaymentTransitionService paymentTransitionService;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
//        OrderRecord order = orderRepository.findByIdAndMerchantId(request.orderId(),merchantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order",request.orderId()));

        OrderRecord order = orderRepository.findByIdAndMerchantIdForUpdate(request.orderId(),merchantId)
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
                .idempotencyKey(UUID.randomUUID().toString())
                .status(PaymentStatus.CREATED)
                .order(order)
                .build();
        payment = paymentRepository.save(payment);

        paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_ATTEMPT);

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
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);

            }
            case PaymentResult.Success success -> {
                log.info("Invalid state");
                return null;
            }
        }
        payment = paymentRepository.save(payment);
        orderRepository.save(order);


        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse capture(UUID merchantId,UUID paymentId) {
//        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId,merchantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment",paymentId));

        Payment payment = paymentRepository.findByIdAndMerchantIdForUpdate(paymentId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment",paymentId));

        paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);

        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(),paymentId);

        if(paymentResult instanceof PaymentResult.Success success){
            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("Payment Captured, paymentId: {}",paymentId);
        } else if (paymentResult instanceof PaymentResult.Failure failure) {
//            payment.setFailedAt(LocalDateTime.now());
            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_FAIL);
            payment.setErrorDescription(failure.errorDescription());
            payment.setErrorCode(failure.errorCode());
            log.warn("Payment capture failed, paymentId: {}",paymentId);
        }

        payment = paymentRepository.save(payment);

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription) {

//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment",paymentId));

        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment",paymentId));

        if(payment.getStatus() != PaymentStatus.AUTHORIZING){
            log.warn("Payment is not in Authorizing state, paymentID: {}, status: {}",paymentId,payment.getStatus());
            return ;
        }

        OrderRecord orderRecord = payment.getOrder();
        if(approve){
            paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getMethod(),paymentId);

            if(captureResult instanceof PaymentResult.Success success){
                paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            }
            else if(captureResult instanceof PaymentResult.Failure failure){
                paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
        }
        else{
            paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);
    }
}
