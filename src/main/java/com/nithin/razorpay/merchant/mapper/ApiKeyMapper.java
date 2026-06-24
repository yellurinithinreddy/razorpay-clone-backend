package com.nithin.razorpay.merchant.mapper;

import com.nithin.razorpay.merchant.dto.request.ApiKeyResponse;
import com.nithin.razorpay.merchant.entities.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {

    List<ApiKeyResponse> toResponseList(List<ApiKey> apiKeys);
}
