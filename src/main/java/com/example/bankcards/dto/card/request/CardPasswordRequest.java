package com.example.bankcards.dto.card.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CardPasswordRequest(

        @NotNull(message = "Card number is required")
        @Size(min = 4, max = 4, message = "Last 4 digits of number")
        @Pattern(regexp = "\\d+", message = "Card number must contain only digits")
        String lastFourDigitsOfNumber,

        @NotNull(message = "Pincode is required")
        @Size(min = 4, max = 4, message = "Pincode must be exactly 4 digits")
        @Pattern(regexp = "\\d+", message = "Pincode must contain only digits")
        String pincode
        ){}