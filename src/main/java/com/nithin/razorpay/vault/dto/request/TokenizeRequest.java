package com.nithin.razorpay.vault.dto.request;

import com.nithin.razorpay.vault.validation.CardExpiry;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

@CardExpiry
public record TokenizeRequest(

        @NotBlank(message = "PAN is required")
        @Pattern(regexp = "^[0-9]{13,16}$",message = "PAN length is invalid")
        @LuhnCheck
        String pan,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV length is invalid")
        String cvv,


        @NotNull(message = "Expiry Month is required")
        @Min(value = 1, message = "Expiry Month should be in the range of 1 to 12")
        @Max(value = 12, message = "Expiry Month should be in the range of 1 to 12")
        Integer expiryMonth,

        @NotNull(message = "Expiry Year is required")
        Integer expiryYear,

        UUID customerId,

        @Size(min = 3,message = "Card Holder Name should at least have 2 characters")
        String cardHolderName
) {
}
