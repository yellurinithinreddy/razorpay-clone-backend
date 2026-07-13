package com.nithin.razorpay.payment.gateway.adapters;

import com.nithin.razorpay.common.entities.Money;
import com.nithin.razorpay.common.enums.PaymentMethod;
import com.nithin.razorpay.payment.gateway.PaymentAdapter;
import com.nithin.razorpay.payment.gateway.dto.PaymentRequest;
import com.nithin.razorpay.payment.gateway.dto.PaymentResult;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.nithin.razorpay.vault.services.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
    public PaymentResult initiate(PaymentRequest request) {

        String token = request.methodDetails().get("token").toString();
        PaymentProcessorResponse paymentProcessorResponse = vaultService.charge(
                request.paymentId(), token,request.amount(),request.methodDetails()
        );


        return switch (paymentProcessorResponse){
            case PaymentProcessorResponse.Pending pending-> new PaymentResult.Pending(pending.processorReference());
            case PaymentProcessorResponse.Failure failure-> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Success success-> new PaymentResult.Success(success.bankReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");
    }
}
