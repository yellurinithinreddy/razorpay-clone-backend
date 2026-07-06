package com.nithin.razorpay.payment.simulator;


import com.nithin.razorpay.common.enums.PaymentStatus;
import com.nithin.razorpay.payment.entities.Payment;
import com.nithin.razorpay.payment.repositories.PaymentRepository;
import com.nithin.razorpay.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {

    private final PaymentRepository paymentRepository;
    private final SimulatorConfig simulatorConfig;
    private final PaymentService paymentService;

    public void processCallBacks(){

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);
        List<Payment> candidates = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING,globalWindow);

        if(candidates.isEmpty()) return ;

        for(Payment payment:candidates){
            simulateCallBack(payment);
        }
    }

    private void simulateCallBack(Payment payment) {
    }

}
