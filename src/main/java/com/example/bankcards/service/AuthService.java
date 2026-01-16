package com.example.bankcards.service;

import com.example.bankcards.dto.auth.request.SignInRequest;
import com.example.bankcards.dto.auth.response.JwtResponse;
import com.example.bankcards.dto.auth.request.SignUpRequest;
import com.example.bankcards.dto.auth.request.RefreshTokenRequest;
import com.example.bankcards.exception.RefreshTokenExpiredException;
import com.example.bankcards.security.JwtCore;
import com.example.bankcards.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtCore jwtCore;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtCore jwtCore) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtCore = jwtCore;
    }

    @Transactional
    public JwtResponse signInAndGenerateTokens(SignInRequest signUpRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpRequest.username(), signUpRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtCore.generateAccessToken(authentication);
        String refreshTokenString = jwtCore.generateRefreshToken(authentication);

        return new JwtResponse(accessToken, refreshTokenString, "Bearer ");
    }


    @Transactional
    public JwtResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        String incomingRefreshToken = refreshTokenRequest.refreshToken();
        if (jwtCore.getExpiryInstantFromJwt(incomingRefreshToken).isBefore(Instant.now())) {
            throw new RefreshTokenExpiredException("Refresh token expired. Please sign in again.");
        }
        User user = (User) userDetailsService.loadUserByUsername(jwtCore.getNameFromJwt(incomingRefreshToken));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        String newAccessToken = jwtCore.generateAccessToken(authentication);
        String newRefreshTokenString = jwtCore.generateRefreshToken(authentication);
        return new JwtResponse(newAccessToken, newRefreshTokenString, "Bearer ");
    }
}
