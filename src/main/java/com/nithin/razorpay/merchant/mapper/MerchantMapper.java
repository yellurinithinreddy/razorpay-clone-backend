package com.nithin.razorpay.merchant.mapper;

import com.nithin.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.nithin.razorpay.merchant.dto.response.MerchantResponse;
import com.nithin.razorpay.merchant.entities.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    @Mapping(target = "merchantStatus",source = "status")
    MerchantResponse toResponse(Merchant merchant);

    Merchant toEntityFromSignUpRequest(MerchantSignupRequest merchantSignupRequest);
}
