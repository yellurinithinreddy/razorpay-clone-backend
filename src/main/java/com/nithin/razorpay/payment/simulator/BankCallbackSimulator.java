package com.nithin.razorpay.payment.simulator;


import com.nithin.razorpay.common.enums.ChaosMode;
import com.nithin.razorpay.common.enums.PaymentStatus;
import com.nithin.razorpay.common.util.RandomizerUtil;
import com.nithin.razorpay.payment.entities.Payment;
import com.nithin.razorpay.payment.repositories.PaymentRepository;
import com.nithin.razorpay.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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

//    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-seconds:5000}")
    public void processCallBacks(){

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);
        List<Payment> candidates = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING,globalWindow);

        log.info("Simulating payment for {} payments",candidates.size());

        if(candidates.isEmpty()) return ;

        for(Payment payment:candidates){
            simulateCallBack(payment);
        }
    }

    private void simulateCallBack(Payment payment) {

        SimulatorConfig.MethodSimulatorConfig methodConfig = simulatorConfig.configFor(payment.getMethod());
        LocalDateTime dueAt = dueAt(payment,methodConfig);

        if(LocalDateTime.now().isBefore(dueAt)) return ;

        ChaosMode chaosMode = simulatorConfig.getChaosMode();
        switch (chaosMode){
            case ChaosMode.SUCCESS -> resolve(payment,true);
            case ChaosMode.FAILURE -> resolve(payment,false);
            case ChaosMode.TIME_OUT -> {
                log.debug("BankCallback simulator: Payment Timed out");
            }
            case ChaosMode.NORMAL,SLOW -> resolve(payment,shouldApprove(payment,methodConfig));
        }
    }

    private boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodConfig.getSuccessRate();
    }

    private void resolve(Payment payment,boolean approve) {

        if(approve){
            String bankRef = "SIM_BANK_REF"+ RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(),true,bankRef,null,null);
        }
        else{
            paymentService.resolveAuthorization(payment.getId(),false,null,"SIM_BANK_ERROR_CODE","Simulated Bank Error Decline");
        }
    }


    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig){
        int range = methodConfig.getMaxDelaySeconds() - methodConfig.getMinDelaySeconds();
        int delaySeconds = methodConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range+1);

        if(simulatorConfig.getChaosMode() == ChaosMode.SLOW){
            delaySeconds = 2*delaySeconds;
        }

        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }

}
