package com.example.bankcards.dto.card.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.LuhnCheck;

public record CardNumberRequest(

        @NotNull(message = "Card number is required")
        @Size(min = 16, max = 16, message = "Card number must be 16 digits")
        @Pattern(regexp = "\\d+", message = "Card number must contain only digits")
        @LuhnCheck(message = "Invalid card number (checksum failed)")
        String number
) {
}
