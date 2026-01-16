package com.example.bankcards.dto.auth.response;

public record JwtResponse( String accessToken,
         String refreshToken,
         String type) {}
