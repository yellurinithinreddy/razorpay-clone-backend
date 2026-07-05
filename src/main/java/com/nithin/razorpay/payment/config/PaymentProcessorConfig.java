package com.nithin.razorpay.payment.config;

import com.nithin.razorpay.common.enums.PaymentMethod;
import com.nithin.razorpay.payment.processor.PaymentProcessor;
import com.nithin.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.nithin.razorpay.payment.processor.strategy.NetBankingProcessor;
import com.nithin.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardPaymentProcessor CardPaymentProcessor;
    private final NetBankingProcessor netBankingProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;
    @Bean
    public Map<PaymentMethod, PaymentProcessor> getPaymentProcessorMap(){
        return Map.of(
                PaymentMethod.CARD,CardPaymentProcessor,
                PaymentMethod.NETBANKING,netBankingProcessor,
                PaymentMethod.UPI,upiPaymentProcessor
        );
    }
}
