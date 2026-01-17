package com.example.bankcards.service;

import com.example.bankcards.dto.card.request.CardCreateRequest;
import com.example.bankcards.dto.card.request.CardPasswordRequest;
import com.example.bankcards.dto.card.request.CardNumberRequest;
import com.example.bankcards.dto.card.request.CardsFilter;
import com.example.bankcards.dto.card.response.CardMaskedResponse;
import com.example.bankcards.dto.card.response.CardResponse;
import com.example.bankcards.dto.transfer.request.TransferRequest;
import com.example.bankcards.dto.transfer.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardsConflictException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardSpecifications;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.util.CardUtil;
import com.example.bankcards.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    @Autowired
    public CardService(CardRepository cardRepository, CardMapper cardMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<CardMaskedResponse> getUserCards(CardsFilter filter, Pageable pageable) {
        User currentUser = SecurityUtils.getCurrentUser();
        Specification<Card> specification = CardSpecifications.build(currentUser, filter);
        Page<Card> cards = cardRepository.findAll(specification, pageable);
        return cards.map(cardMapper::toMaskedDto);
    }

    @Transactional()
    public CardMaskedResponse requestToBlock(CardNumberRequest cardNumberRequest) {
        User currentUser = SecurityUtils.getCurrentUser();
        Card card = cardRepository.findByNumberAndOwner(cardNumberRequest.number(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus("BLOCK_REQUESTED");
        cardRepository.save(card);
        return cardMapper.toMaskedDto(card);
    }

    @Transactional
    public TransferResponse transferBetweenOwnCards(TransferRequest request) {
        if (request.fromNumber().equals(request.toNumber())) {
            throw new CardsConflictException("Source and target cards must be different");
        }
        User currentUser = SecurityUtils.getCurrentUser();

        Card fromCard = cardRepository.findByNumberAndOwner(request.fromNumber(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Source card not found"));
        Card toCard = cardRepository.findByNumberAndOwner(request.toNumber(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Target card not found"));

        checkCardValidity(fromCard);
        checkCardValidity(toCard);

        if (!passwordEncoder.matches(request.pincode(), fromCard.getPincode())) {
            int attempts = fromCard.getFailedAttempts() + 1;
            fromCard.setFailedAttempts(attempts);

            if (attempts >= 3) {
                fromCard.setStatus("BLOCKED");
                cardRepository.save(fromCard);
                return new TransferResponse("Card has been blocked");
            }

            cardRepository.save(fromCard);
            return new TransferResponse("Invalid pincode. Attempts remaining: " + (3 - attempts));
        }

        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        fromCard.setFailedAttempts(0);
        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        return new TransferResponse("Transfer successful");
    }

    @Transactional
    public CardMaskedResponse activate(CardNumberRequest request) {
        Card card = cardRepository.findByNumber(request.number())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus("ACTIVE");
        cardRepository.save(card);
        return cardMapper.toMaskedDto(card);
    }

    @Transactional
    public CardMaskedResponse block(CardNumberRequest request) {
        Card card = cardRepository.findByStatusAndNumber("BLOCK_REQUESTED", request.number())
                .orElseThrow(() -> new ResourceNotFoundException("Block request not found"));
        card.setStatus("BLOCKED");
        cardRepository.save(card);
        return cardMapper.toMaskedDto(card);
    }

    @Transactional
    public CardMaskedResponse adminCreate(CardCreateRequest request) {
        Card card = new Card();
        User owner = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        card.setOwner(owner);
        card.setPincode(passwordEncoder.encode(request.pincode()));
        card.setBalance(request.balance());
        card.setExpiryDate(LocalDate.now().plusYears(4));
        card.setStatus("ACTIVE");
        card.setNumber(generateUniqueCardNumber());
        return cardMapper.toMaskedDto(cardRepository.save(card));
    }

    @Transactional
    public void adminDelete(Long id) {
        cardRepository.deleteById(id);
    }

    @Transactional
    public CardResponse getCard(CardPasswordRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();
        List<Card> cards = cardRepository.findByOwner(currentUser);
        Card card = cards.stream()
                .filter(cardTemp -> {
                    String fullNumber = cardTemp.getNumber();
                    return fullNumber != null && fullNumber.endsWith(request.lastFourDigitsOfNumber());
                }).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Card with last 4 digits " + request.lastFourDigitsOfNumber() + " not found"
                ));


        if ("BLOCKED".equals(card.getStatus())) {
            throw new CardsConflictException("Card is blocked");
        }

        if (!passwordEncoder.matches(request.pincode(), card.getPincode())) {
            int attempts = card.getFailedAttempts() + 1;
            card.setFailedAttempts(attempts);

            if (attempts >= 3) {
                card.setStatus("BLOCKED");
                cardRepository.save(card);
                throw new CardsConflictException("Too many attempts. Card has been blocked.");
            }

            cardRepository.save(card);
            throw new CardsConflictException("Invalid pincode. Attempts remaining: " + (3 - attempts));
        }

        card.setFailedAttempts(0);
        cardRepository.save(card);

        return cardMapper.toDto(card);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> adminGetAll(CardsFilter filter, Pageable pageable) {
        Specification<Card> specification = CardSpecifications.build(filter);
        Page<Card> cards = cardRepository.findAll(specification, pageable);
        return cards.map(cardMapper::toDto);
    }

    private String generateUniqueCardNumber() {
        String number;
        do {
            number = CardUtil.generate();
        } while (cardRepository.existsByNumber(number));
        return number;
    }

    private void checkCardValidity(Card card) {
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            if (!"EXPIRED".equals(card.getStatus())) {
                card.setStatus("EXPIRED");
                cardRepository.save(card);
            }
            throw new CardsConflictException("Card has expired");
        }

        if (!"ACTIVE".equals(card.getStatus())) {
            throw new CardsConflictException("Card is not active. Status: " + card.getStatus());
        }
    }
}