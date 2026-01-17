package com.example.bankcards.entity;

import com.example.bankcards.util.EncryptionConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Setter
@Getter
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = EncryptionConverter.class)
    @Column(unique = true, nullable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false)
    private int failedAttempts = 0;

    @Column(nullable = false)
    private BigDecimal balance;

}
