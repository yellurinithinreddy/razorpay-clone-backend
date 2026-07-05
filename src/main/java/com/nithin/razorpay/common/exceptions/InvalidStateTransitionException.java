package com.nithin.razorpay.common.exceptions;

import com.nithin.razorpay.common.enums.PaymentEvent;
import com.nithin.razorpay.common.enums.PaymentStatus;
import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends RuntimeException{

    private final String fromStatus;
    private final String toEvent;

    public InvalidStateTransitionException(String status,String event){
        super("Invalid transition from "+status+" to "+event);
        this.fromStatus = status;
        this.toEvent = event;
    }
}
