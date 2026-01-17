package com.example.bankcards.controller;

import com.example.bankcards.dto.card.request.CardCreateRequest;
import com.example.bankcards.dto.card.request.CardPasswordRequest;
import com.example.bankcards.dto.card.request.CardNumberRequest;
import com.example.bankcards.dto.card.request.CardsFilter;
import com.example.bankcards.dto.card.response.CardMaskedResponse;
import com.example.bankcards.dto.card.response.CardResponse;
import com.example.bankcards.dto.transfer.request.TransferRequest;
import com.example.bankcards.dto.transfer.response.TransferResponse;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my_cards")
    public ResponseEntity<Page<CardMaskedResponse>> getUserCards(
            @RequestBody(required = false) CardsFilter filter,
            @PageableDefault(sort = "number", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(cardService.getUserCards(filter,pageable));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/get_details")
    public ResponseEntity<CardResponse> getCardDetails(
            @Valid @RequestBody CardPasswordRequest cardPasswordRequest
    ){
        return ResponseEntity.status(HttpStatus.OK).body(cardService.getCard(cardPasswordRequest));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/block_request")
    public ResponseEntity<CardMaskedResponse> requestToBlockCard(
            @Valid @RequestBody CardNumberRequest cardNumberRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(cardService.requestToBlock(cardNumberRequest));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/transfer")
    public ResponseEntity<TransferResponse> transferBetweenOwnCards(
            @Valid @RequestBody TransferRequest transferRequest
    ){
        return ResponseEntity.status(HttpStatus.OK).body(cardService.transferBetweenOwnCards(transferRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate")
    public ResponseEntity<CardMaskedResponse> activateCard(
           @Valid @RequestBody CardNumberRequest cardNumberRequest
    ){
        return ResponseEntity.status(HttpStatus.OK).body(cardService.activate(cardNumberRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/block")
    public ResponseEntity<CardMaskedResponse> blockCard(
           @Valid @RequestBody CardNumberRequest cardNumberRequest
    ){
        return ResponseEntity.status(HttpStatus.OK).body(cardService.block(cardNumberRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CardMaskedResponse> createCard(
           @Valid @RequestBody CardCreateRequest cardRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.adminCreate(cardRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCard(
            @PathVariable Long id
    ){
        cardService.adminDelete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get_all")
    public ResponseEntity<Page<CardResponse>> getAllCards(
            @RequestBody(required = false) CardsFilter filter,
            @PageableDefault(sort = "number", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.status(HttpStatus.OK).body(cardService.adminGetAll(filter,pageable));
    }

}
