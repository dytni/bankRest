package com.example.bankcards.dto.card.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CardCreateRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Balance is required")
        @PositiveOrZero(message = "Balance must be zero or positive")
        @Digits(integer = 15, fraction = 2, message = "Invalid balance format")
        BigDecimal balance,

        @NotNull(message = "Pincode is required")
        @Size(min = 4, max = 4, message = "Pincode must be exactly 4 digits")
        @Pattern(regexp = "\\d+", message = "Pincode must contain only digits")
        String pincode
) {
}
