package com.nithin.razorpay.common.audit;

import com.nithin.razorpay.merchant.security.MerchantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String>{

    private final MerchantContext merchantContext;


    @Override
    public Optional<String> getCurrentAuditor() {

        try{

            String keyId = merchantContext.getKeyId();
            String merchantId = String.valueOf(merchantContext.getMerchantId());
            if(keyId != null && !keyId.isBlank()){
                return Optional.of(keyId);
            }

            if(merchantId != null && !merchantId.isBlank()){
                return Optional.of("merchant_id "+merchantId);
            }
        }catch (Exception ignored){

        }

        return Optional.of("SYSTEM");
    }
}
