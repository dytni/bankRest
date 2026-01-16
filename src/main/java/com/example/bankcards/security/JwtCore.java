package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtCore {

    @Value("${testing.app.secret}")
    private String token;

    @Value("${testing.app.expirationMs}")
    private Integer expiresAccess;


    @Value("${testing.app.refreshExpirationMs}")
    private Integer expiresRefresh;



    public String generateAccessToken(Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expiresAccess))
                .signWith(getSigningKey())
                .compact();
    }

    public Instant getExpiryInstantFromJwt(String jwt) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        return claims.getExpiration().toInstant();
    }

    public String generateRefreshToken(Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expiresRefresh))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = this.token.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getNameFromJwt(String jwt) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        return claims.getSubject();
    }
}
