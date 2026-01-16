package com.example.bankcards.dto.auth.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInRequest(@NotBlank(message = "Username is required")
                            @Email(message = "Email should be valid")
                            String username,

                            @NotBlank(message = "Password is required")
                            @Size(min = 6, message = "Password must be at least 6 characters")
                            String password
){
}