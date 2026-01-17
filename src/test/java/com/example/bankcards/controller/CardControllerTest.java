package com.example.bankcards.controller;


import com.example.bankcards.BaseControllerTest;
import com.example.bankcards.dto.card.request.CardNumberRequest;
import com.example.bankcards.dto.card.request.CardPasswordRequest;
import com.example.bankcards.dto.card.response.CardMaskedResponse;
import com.example.bankcards.dto.card.response.CardResponse;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardService cardService;


    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Должен вернуть список замаскированных карт пользователя")
    void getUserCards_ShouldReturnOk() throws Exception {
        CardMaskedResponse maskedCard = new CardMaskedResponse("**** **** **** 4444", "ACTIVE", "TEST", LocalDate.now());
        when(cardService.getUserCards(any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(maskedCard)));

        mockMvc.perform(get("/cards/my_cards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].maskedNumber").value("**** **** **** 4444"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Должен вернуть полные детали карты при правильном PIN-коде")
    void getCardDetails_ShouldReturnFullDetails() throws Exception {
        CardPasswordRequest request = new CardPasswordRequest("4444", "1111");
        CardResponse fullCard = new CardResponse("1111222233334444", "TEST", "TEST", new BigDecimal("1000.00"), LocalDate.now());


        when(cardService.getCard(any())).thenReturn(fullCard);

        mockMvc.perform(get("/cards/get_details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("1111222233334444"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Запрос на блокировку должен возвращать статус 200")
    void requestToBlockCard_ShouldReturnOk() throws Exception {
        CardNumberRequest request = new CardNumberRequest("1111222233334444");
        CardMaskedResponse response = new CardMaskedResponse("**** 4444", "BLOCK_REQUESTED", "TEST", LocalDate.now());

        when(cardService.requestToBlock(any())).thenReturn(response);

        mockMvc.perform(patch("/cards/block_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCK_REQUESTED"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Админ должен успешно активировать карту")
    void activateCard_AsAdmin_ShouldReturnOk() throws Exception {
        CardNumberRequest request = new CardNumberRequest("1111222233334444");
        CardMaskedResponse response = new CardMaskedResponse("**** 4444", "ACTIVE", "TEST", LocalDate.now());

        when(cardService.activate(any())).thenReturn(response);

        mockMvc.perform(patch("/cards/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Обычный пользователь не может активировать карты (403)")
    void activateCard_AsUser_ShouldReturnForbidden() throws Exception {
        CardNumberRequest request = new CardNumberRequest("1111222233334444");

        mockMvc.perform(patch("/cards/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Админ должен иметь возможность удалить карту")
    void deleteCard_AsAdmin_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/cards/delete/1"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Должен вернуть 400, если номер карты пустой")
    void requestToBlock_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        CardNumberRequest request = new CardNumberRequest("");

        mockMvc.perform(patch("/cards/block_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
