package com.example.bankcards.config.openapi;

import com.example.bankcards.dto.auth.request.SignUpRequest;
import com.example.bankcards.dto.user.request.UserCreateRequest;
import com.example.bankcards.dto.user.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Users", description = "Endpoints for user profile and administration")
public interface UserApi {

    @Operation(summary = "Update current profile", description = "Allows authorized user to update their own profile data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Full authentication is required"),
            @ApiResponse(responseCode = "404", description = "Current user not found")
    })
    ResponseEntity<UserResponse> updateCurrentUser(SignUpRequest updateRequest);

    @Operation(summary = "Delete current profile", description = "Allows authorized user to delete their own account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Full authentication is required"),
            @ApiResponse(responseCode = "404", description = "Current user not found")
    })
    ResponseEntity<UserResponse> deleteCurrentUser();

    @Operation(summary = "Admin: Create user", description = "Admin creates a new user with specific roles.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin role required"),
            @ApiResponse(responseCode = "409", description = "Conflict: Username already exists")
    })
    ResponseEntity<UserResponse> createUser(UserCreateRequest createRequest);

    @Operation(summary = "Admin: Update user", description = "Admin updates data of any user by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin role required"),
            @ApiResponse(responseCode = "404", description = "User with given ID not found")
    })
    ResponseEntity<UserResponse> updateUser(UserCreateRequest createRequest, Long id);

    @Operation(summary = "Admin: Delete user", description = "Admin deletes any user by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin role required"),
            @ApiResponse(responseCode = "404", description = "User with given ID not found")
    })
    ResponseEntity<?> deleteUser(Long id);

    @Operation(summary = "Admin: Get user by ID", description = "Admin retrieves full information about a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin role required"),
            @ApiResponse(responseCode = "404", description = "User with given ID not found")
    })
    ResponseEntity<UserResponse> getUser(Long id);

    @Operation(summary = "Admin: Get all users", description = "Returns a paginated list of all registered users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin role required")
    })
    ResponseEntity<Page<UserResponse>> getAll(Pageable pageable);
}