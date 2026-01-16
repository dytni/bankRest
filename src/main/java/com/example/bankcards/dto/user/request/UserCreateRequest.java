package com.example.bankcards.dto.user.request;

import com.example.bankcards.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
                                @NotBlank(message = "Username is required")
                                @Email(message = "Email should be valid")
                                String username,

                                @NotBlank(message = "Last name is required")
                                String lastName,

                                @NotBlank(message = "First name is required")
                                String firstName,

                                @NotBlank(message = "Second name is required")
                                String secondName,

                                @NotNull(message = "Role is required")
                                Role role,

                                @NotBlank(message = "Password is required")
                                @Size(min = 6, message = "Password must be at least 6 characters")
                                String password
) {
}
