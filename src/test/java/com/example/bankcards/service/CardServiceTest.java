package com.example.bankcards.service;

import com.example.bankcards.dto.card.request.CardNumberRequest;
import com.example.bankcards.dto.card.request.CardPasswordRequest;
import com.example.bankcards.dto.transfer.request.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardsConflictException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CardService cardService;

    private User testUser;
    private Card testCard;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test_user");

        testCard = new Card();
        testCard.setNumber("1234567890123456");
        testCard.setOwner(testUser);
        testCard.setPincode("encoded_pincode");
        testCard.setStatus("ACTIVE");
        testCard.setBalance(new BigDecimal("1000.00"));
        testCard.setExpiryDate(LocalDate.now().plusYears(1));
        testCard.setFailedAttempts(0);
    }

    @Test
    @DisplayName("Блокировка карты: успешный запрос на блокировку пользователем")
    void requestToBlock_Success() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            CardNumberRequest request = new CardNumberRequest(testCard.getNumber());

            when(cardRepository.findByNumberAndOwner(testCard.getNumber(), testUser))
                    .thenReturn(Optional.of(testCard));

            cardService.requestToBlock(request);

            assertEquals("BLOCK_REQUESTED", testCard.getStatus());
            verify(cardRepository).save(testCard);
        }
    }

    @Test
    @DisplayName("Перевод: ошибка при недостаточном балансе")
    void transfer_InsufficientFunds_ShouldThrowException() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(testUser);

            Card fromCard = testCard;
            Card toCard = new Card();
            toCard.setNumber("6543210987654321");
            toCard.setStatus("ACTIVE");
            toCard.setExpiryDate(LocalDate.now().plusYears(1));

            TransferRequest request = new TransferRequest(
                    fromCard.getNumber(), toCard.getNumber(), new BigDecimal("5000.00"), "1234"
            );

            when(cardRepository.findByNumberAndOwner(fromCard.getNumber(), testUser)).thenReturn(Optional.of(fromCard));
            when(cardRepository.findByNumberAndOwner(toCard.getNumber(), testUser)).thenReturn(Optional.of(toCard));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            assertThrows(InsufficientFundsException.class, () -> cardService.transferBetweenOwnCards(request));
        }
    }

    @Test
    @DisplayName("Админ: активация карты")
    void activate_Success() {
        CardNumberRequest request = new CardNumberRequest("1234");
        testCard.setStatus("INACTIVE");

        when(cardRepository.findByNumber("1234")).thenReturn(Optional.of(testCard));

        cardService.activate(request);

        assertEquals("ACTIVE", testCard.getStatus());
        verify(cardRepository).save(testCard);
    }

    @Test
    @DisplayName("Получение данных карты: ошибка при неверном пин-коде (без блокировки)")
    void getCard_WrongPincode_ShouldIncrementAttempts() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(testUser);

            CardPasswordRequest request = new CardPasswordRequest("3456", "wrong_pin");

            when(cardRepository.findByOwner(testUser)).thenReturn(List.of(testCard));
            when(passwordEncoder.matches("wrong_pin", testCard.getPincode())).thenReturn(false);

            CardsConflictException exception = assertThrows(CardsConflictException.class,
                    () -> cardService.getCard(request));

            assertTrue(exception.getMessage().contains("Invalid pincode"));
            assertEquals(1, testCard.getFailedAttempts());
            verify(cardRepository).save(testCard);
        }
    }

    @Test
    @DisplayName("Удаление карты администратором")
    void adminDelete_Success() {
        Long cardId = 100L;

        cardService.adminDelete(cardId);

        verify(cardRepository, times(1)).deleteById(cardId);
    }
}
