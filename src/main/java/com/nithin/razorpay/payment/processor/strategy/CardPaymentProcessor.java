package com.nithin.razorpay.payment.processor.strategy;

import com.nithin.razorpay.common.util.RandomizerUtil;
import com.nithin.razorpay.payment.processor.PaymentProcessor;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardPaymentProcessor implements PaymentProcessor {

    private static final String PAN_CARD_DECLINED = "400000000000002";
    private static final String PAN_CARD_EXPIRED = "400000000000069";
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        if(PAN_CARD_DECLINED.equals(request.pan())){
            log.warn("Card Declined");
            return new PaymentProcessorResponse.Failure("CARD_DECLINED","Card declined by bank");
        }

        if(PAN_CARD_EXPIRED.equals(request.pan())){
            log.warn("");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED","CARD is expired");
        }

        String processorReference = "CARD_PROCESSOR_"+ RandomizerUtil.randomBase64(16);


        return new PaymentProcessorResponse.Pending(processorReference);
    }
}
