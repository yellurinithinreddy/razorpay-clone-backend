package com.nithin.razorpay.merchant.services.impl;

import com.nithin.razorpay.common.enums.MerchantStatus;
import com.nithin.razorpay.common.enums.UserRole;
import com.nithin.razorpay.common.exceptions.DuplicateResourceException;
import com.nithin.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.nithin.razorpay.merchant.dto.response.MerchantResponse;
import com.nithin.razorpay.merchant.entities.AppUser;
import com.nithin.razorpay.merchant.entities.Merchant;
import com.nithin.razorpay.merchant.repositories.AppUserRepository;
import com.nithin.razorpay.merchant.repositories.MerchantRepository;
import com.nithin.razorpay.merchant.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUSerRepository;

    @Override
    public MerchantResponse create(MerchantSignupRequest merchantSignupRequest) {
        if(merchantRepository.existsByEmail(merchantSignupRequest.email())){
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL","Merchant with email already exists: "+merchantSignupRequest.email());
        }

        Merchant merchant = Merchant.builder()
                .name(merchantSignupRequest.name())
                .email(merchantSignupRequest.email())
                .businessName(merchantSignupRequest.businessName())
                .businessType(merchantSignupRequest.businessType())
                .build();

        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(merchantSignupRequest.email())
                .passwordHash(merchantSignupRequest.password())
                .merchant(merchant)
                .role(UserRole.OWNER)
                .build();

        appUser = appUSerRepository.save(appUser);

        return new MerchantResponse(merchant.getId(),merchant.getName(),merchant.getEmail(), merchant.getBusinessName(),merchant.getBusinessType(), MerchantStatus.PENDING_KYC);

    }
}
