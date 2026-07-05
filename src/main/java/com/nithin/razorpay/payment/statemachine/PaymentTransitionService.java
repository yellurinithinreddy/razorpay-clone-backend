package com.nithin.razorpay.payment.statemachine;

import com.nithin.razorpay.common.enums.PaymentActor;
import com.nithin.razorpay.common.enums.PaymentEvent;
import com.nithin.razorpay.common.enums.PaymentStatus;
import com.nithin.razorpay.payment.entities.Payment;
import com.nithin.razorpay.payment.entities.PaymentTransitionLog;
import com.nithin.razorpay.payment.repositories.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentStateMachine paymentStateMachine;
    private PaymentTransitionLogRepository paymentTransitionLogRepository;

    public PaymentStatus apply(Payment payment, PaymentEvent event){
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(),event);
        payment.setStatus(next);

        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .occuredAt(LocalDateTime.now())
                .fromStatus(payment.getStatus())
                .toStatus(next)
                .actor(PaymentActor.SYSTEM) // TODO : get from security context holder
                .event(event)
                .build();

        paymentTransitionLogRepository.save(log);
        return next;
    }

}
