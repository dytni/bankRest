package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.request.SignInRequest;
import com.example.bankcards.dto.auth.response.JwtResponse;
import com.example.bankcards.dto.auth.request.SignUpRequest;
import com.example.bankcards.dto.auth.request.RefreshTokenRequest;
import com.example.bankcards.dto.user.response.UserResponse;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.signInAndGenerateTokens(signInRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(signUpRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshAccessToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.refreshAccessToken(refreshRequest));
    }



}
