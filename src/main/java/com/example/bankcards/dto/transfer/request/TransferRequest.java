package com.example.bankcards.dto.transfer.request;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.math.BigDecimal;

public record TransferRequest(

        @NotNull(message = "Card number is required")
        @Size(min = 16, max = 16, message = "Card number must be 16 digits")
        @Pattern(regexp = "\\d+", message = "Card number must contain only digits")
        @LuhnCheck(message = "Invalid card number (checksum failed)")
        String fromNumber,

        @NotNull(message = "Card number is required")
        @Size(min = 16, max = 16, message = "Card number must be 16 digits")
        @Pattern(regexp = "\\d+", message = "Card number must contain only digits")
        @LuhnCheck(message = "Invalid card number (checksum failed)")
        String toNumber,

        @NotNull(message = "Balance is required")
        @PositiveOrZero(message = "Balance must be zero or positive")
        @Digits(integer = 15, fraction = 2, message = "Invalid balance format")
        BigDecimal amount,

        @NotNull(message = "Pincode is required")
        @Size(min = 4, max = 4, message = "Pincode must be exactly 4 digits")
        @Pattern(regexp = "\\d+", message = "Pincode must contain only digits")
        String pincode
        ) {
}
