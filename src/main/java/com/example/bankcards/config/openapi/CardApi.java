package com.example.bankcards.config.openapi;


import com.example.bankcards.dto.card.request.*;
import com.example.bankcards.dto.card.response.*;
import com.example.bankcards.dto.transfer.request.TransferRequest;
import com.example.bankcards.dto.transfer.response.TransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Cards", description = "Endpoints for managing bank cards and transfers")
public interface CardApi {

    @Operation(summary = "Get current user cards", description = "Returns a paginated list of cards belonging to the authorized user (masked).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of masked cards retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource")
    })
    ResponseEntity<Page<CardMaskedResponse>> getUserCards(CardsFilter filter, Pageable pageable);

    @Operation(summary = "Get full card details", description = "Returns full card details after PIN verification. Handles failed attempts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PIN verified, full details returned"),
            @ApiResponse(responseCode = "401", description = "Incorrect PIN code provided"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "409", description = "Card is blocked or too many failed attempts")
    })
    ResponseEntity<CardResponse> getCardDetails(CardPasswordRequest request);

    @Operation(summary = "Request card block", description = "Allows user to request a block for their own card.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Block request successfully created"),
            @ApiResponse(responseCode = "404", description = "Card not found or doesn't belong to the user")
    })
    ResponseEntity<CardMaskedResponse> requestToBlockCard(CardNumberRequest request);

    @Operation(summary = "Transfer between own cards", description = "Transfers money between two cards belonging to the same user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
            @ApiResponse(responseCode = "409", description = "Incorrect PIN, same card numbers, or card is blocked"),
            @ApiResponse(responseCode = "422", description = "Insufficient funds for the operation")
    })
    ResponseEntity<TransferResponse> transferBetweenOwnCards(TransferRequest request);

    @Operation(summary = "Admin: Activate card", description = "Admin confirms a card activation request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card successfully activated"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin role required"),
            @ApiResponse(responseCode = "404", description = "Card or activation request not found")
    })
    ResponseEntity<CardMaskedResponse> activateCard(CardNumberRequest request);

    @Operation(summary = "Admin: Block card", description = "Admin confirms a card block request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card successfully blocked by admin"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin role required"),
            @ApiResponse(responseCode = "404", description = "Block request not found")
    })
    ResponseEntity<CardMaskedResponse> blockCard(CardNumberRequest request);

    @Operation(summary = "Admin: Create card", description = "Admin creates a new card for a specific user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data (negative balance, etc.)"),
            @ApiResponse(responseCode = "404", description = "Target user not found")
    })
    ResponseEntity<CardMaskedResponse> createCard(CardCreateRequest request);

    @Operation(summary = "Admin: Delete card", description = "Permanently deletes a card by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin role required"),
            @ApiResponse(responseCode = "404", description = "Card ID not found")
    })
    ResponseEntity<?> deleteCard(Long id);

    @Operation(summary = "Admin: Get all cards", description = "Returns a paginated list of all cards in the system (full data).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Full list of cards retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin role required")
    })
    ResponseEntity<Page<CardResponse>> getAllCards(CardsFilter filter, Pageable pageable);
}