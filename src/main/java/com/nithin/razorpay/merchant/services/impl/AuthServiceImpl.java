package com.nithin.razorpay.merchant.services.impl;

import com.nithin.razorpay.common.enums.MerchantStatus;
import com.nithin.razorpay.common.enums.UserRole;
import com.nithin.razorpay.common.exceptions.DuplicateResourceException;
import com.nithin.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.nithin.razorpay.merchant.dto.response.MerchantResponse;
import com.nithin.razorpay.merchant.entities.AppUser;
import com.nithin.razorpay.merchant.entities.Merchant;
import com.nithin.razorpay.merchant.mapper.MerchantMapper;
import com.nithin.razorpay.merchant.repositories.AppUserRepository;
import com.nithin.razorpay.merchant.repositories.MerchantRepository;
import com.nithin.razorpay.merchant.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUSerRepository;
    private final MerchantMapper merchantMapper;

    @Override
    @Transactional
    public MerchantResponse create(MerchantSignupRequest merchantSignupRequest) {
        if(merchantRepository.existsByEmail(merchantSignupRequest.email())){
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL","Merchant with email already exists: "+merchantSignupRequest.email());
        }

        Merchant merchant = merchantMapper.toEntityFromSignUpRequest(merchantSignupRequest);
        merchant.setStatus(MerchantStatus.PENDING_KYC);

        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(merchantSignupRequest.email())
                .passwordHash(merchantSignupRequest.password())
                .merchant(merchant)
                .role(UserRole.OWNER)
                .build();

        appUser = appUSerRepository.save(appUser);

        return merchantMapper.toResponse(merchant);

    }
}
