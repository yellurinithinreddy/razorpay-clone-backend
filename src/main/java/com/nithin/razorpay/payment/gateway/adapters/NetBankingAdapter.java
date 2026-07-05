package com.nithin.razorpay.payment.gateway.adapters;

import com.nithin.razorpay.common.enums.PaymentMethod;
import com.nithin.razorpay.payment.gateway.PaymentAdapter;
import com.nithin.razorpay.payment.gateway.dto.PaymentRequest;
import com.nithin.razorpay.payment.gateway.dto.PaymentResult;
import com.nithin.razorpay.payment.processor.PaymentProcessorRouter;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.nithin.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class NetBankingAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        log.info("Initiate Payment with NET BANKING, paymentId: {}",request.paymentId());

        try{

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(
                    request.paymentId(), PaymentMethod.NETBANKING,request.amount(),request.methodDetails()
            );

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);


            return switch (paymentProcessorResponse) {
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponse.Success success ->
                        new PaymentResult.Success(success.bankReference());
            };
        }catch(Exception e){
            log.warn("NET BANKING failed, paymentId: {}",request.paymentId());
            return new PaymentResult.Failure("NET BANKING FAILED",e.getMessage());

        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("NBK_REF");
    }
}
