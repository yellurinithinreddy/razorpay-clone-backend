package com.nithin.razorpay.payment.processor.strategy;

import com.nithin.razorpay.common.util.RandomizerUtil;
import com.nithin.razorpay.payment.processor.PaymentProcessor;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class NetBankingProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";
        String bankCode = request.methodDetails()!=null ? request.methodDetails().get("BANK").toString() : null;

        //simulation
        if(BANK_CODE_FAIL.equals(bankCode)){
            return new PaymentProcessorResponse.Failure("BANK_REJECTED",
                    "Bank rejected the transaction registration");
        }

        String processorRef = "NBK_PROCESSOR_"+ RandomizerUtil.randomBase64(16);

        String redirectRef = "http://REDIRECT_BANK.com/"+processorRef;

        return new PaymentProcessorResponse.Success(processorRef,redirectRef);
    }
}
