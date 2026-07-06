package com.nithin.razorpay.vault.validation;

import com.nithin.razorpay.vault.dto.request.TokenizeRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.YearMonth;

public class CardExpiryValidator implements ConstraintValidator<CardExpiry, TokenizeRequest> {
    @Override
    public boolean isValid(TokenizeRequest tokenizeRequest, ConstraintValidatorContext constraintValidatorContext) {
        if(tokenizeRequest == null) return false;
        Integer expiryYear = tokenizeRequest.expiryYear();
        Integer expiryMonth = tokenizeRequest.expiryYear();

        YearMonth expiry = YearMonth.of(expiryYear,expiryMonth);
        YearMonth now = YearMonth.now();

        return !expiry.isBefore(now);
    }
}
