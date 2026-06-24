package com.nithin.razorpay.payment.mapper;

import com.nithin.razorpay.payment.dto.response.OrderResponse;
import com.nithin.razorpay.payment.entities.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse toResponse(OrderRecord order);
}
