package com.example.bankcards.config.openapi;

import com.example.bankcards.dto.auth.request.RefreshTokenRequest;
import com.example.bankcards.dto.auth.request.SignInRequest;
import com.example.bankcards.dto.auth.request.SignUpRequest;
import com.example.bankcards.dto.auth.response.JwtResponse;
import com.example.bankcards.dto.user.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token management.")
public interface AuthApi {

    @Operation(summary = "User Login", description = "Authenticates user and returns access and refresh tokens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    ResponseEntity<JwtResponse> authenticateUser(SignInRequest signInRequest);

    @Operation(summary = "User Registration", description = "Creates a new user account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "409", description = "User with this username already exists"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    ResponseEntity<UserResponse> registerUser(SignUpRequest signUpRequest);

    @Operation(summary = "Refresh Token", description = "Generates a new access token using a valid refresh token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens successfully refreshed",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    ResponseEntity<JwtResponse> refreshAccessToken(RefreshTokenRequest refreshRequest);
}