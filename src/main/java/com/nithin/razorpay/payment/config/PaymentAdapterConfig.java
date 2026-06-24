package com.nithin.razorpay.payment.config;

import com.nithin.razorpay.common.enums.PaymentMethod;
import com.nithin.razorpay.payment.gateway.PaymentAdapter;
import com.nithin.razorpay.payment.gateway.adapters.CardPaymentAdapter;
import com.nithin.razorpay.payment.gateway.adapters.NetBankingAdapter;
import com.nithin.razorpay.payment.gateway.adapters.UpiPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {


    @Bean
    public Map<PaymentMethod, PaymentAdapter> getPaymentAdapterMap(){
        return Map.of(
                PaymentMethod.CARD,new CardPaymentAdapter(),
                PaymentMethod.NETBANKING,new NetBankingAdapter(),
                PaymentMethod.UPI,new UpiPaymentAdapter()
        );
    }
}
