package com.nithin.razorpay.merchant.mapper;

import com.nithin.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.nithin.razorpay.merchant.entities.MerchantWebhookConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantWebhookConfigMapper {

    @Mapping(target = "webhookSecret",source = "rawSecret")
    WebhookConfigResponse toResponse(MerchantWebhookConfig merchantWebhookConfig,String rawSecret);
}
