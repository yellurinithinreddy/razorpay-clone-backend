package com.nithin.razorpay.vault.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {CardExpiryValidator.class}
)
public @interface CardExpiry {

    String message() default "Card Expired";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
