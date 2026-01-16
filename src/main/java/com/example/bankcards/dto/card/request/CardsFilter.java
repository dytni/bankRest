package com.example.bankcards.dto.card.request;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class CardsFilter {
    private String status;
    private String number;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    private BigDecimal balance;
}
