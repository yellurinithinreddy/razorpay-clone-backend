package com.nithin.razorpay.payment.config;

import com.nithin.razorpay.common.enums.PaymentMethod;
import com.nithin.razorpay.payment.gateway.PaymentAdapter;
import com.nithin.razorpay.payment.gateway.adapters.CardPaymentAdapter;
import com.nithin.razorpay.payment.gateway.adapters.NetBankingAdapter;
import com.nithin.razorpay.payment.gateway.adapters.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> getPaymentAdapterMap(){
        return Map.of(
                PaymentMethod.CARD,cardPaymentAdapter,
                PaymentMethod.NETBANKING,netBankingAdapter,
                PaymentMethod.UPI,upiPaymentAdapter
        );
    }
}
