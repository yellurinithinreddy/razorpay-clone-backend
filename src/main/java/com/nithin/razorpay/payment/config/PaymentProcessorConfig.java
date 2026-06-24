package com.nithin.razorpay.payment.config;

import com.nithin.razorpay.common.enums.PaymentMethod;
import com.nithin.razorpay.payment.processor.PaymentProcessor;
import com.nithin.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.nithin.razorpay.payment.processor.strategy.NetBankingProcessor;
import com.nithin.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethod, PaymentProcessor> getPaymentProcessorMap(){
        return Map.of(
                PaymentMethod.CARD,new CardPaymentProcessor(),
                PaymentMethod.NETBANKING,new NetBankingProcessor(),
                PaymentMethod.UPI,new UpiPaymentProcessor()
        );
    }
}
