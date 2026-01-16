package com.example.bankcards.dto.card.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponse (
        String number,
        String status,
        String ownerName,
        BigDecimal balance,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/yy")
        LocalDate expiryDate
){}
