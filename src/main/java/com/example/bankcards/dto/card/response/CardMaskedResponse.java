package com.example.bankcards.dto.card.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record CardMaskedResponse(
        String maskedNumber,
        String status,
        String ownerName,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/yy")
        LocalDate expiryDate
){}
