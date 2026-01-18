package com.example.bankcards.controller;

import com.example.bankcards.BaseControllerTest;
import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.config.openapi.UserApi;
import com.example.bankcards.dto.auth.request.SignUpRequest;
import com.example.bankcards.dto.user.request.UserCreateRequest;
import com.example.bankcards.dto.user.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.security.TokenFilter;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, UserApi.class})
@Import({SecurityConfig.class, TokenFilter.class})
@EnableMethodSecurity
class UserControllerTest extends BaseControllerTest {

    @MockitoBean
    private UserService userService;


    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_AsAdmin_ShouldReturnOk() throws Exception {
        when(userService.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/user/get_all"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/user/get_all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCurrentUser_AsUser_ShouldReturnOk() throws Exception {
        SignUpRequest request = new SignUpRequest("user@gmail.com", "Ivanov", "Ivan", "Ivanovich", "1111", "1111");
        UserResponse response = new UserResponse(1L, "user@gmail.com", "Ivanov Ivan Ivanovich");

        when(userService.updateUser(any(SignUpRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user@gmail.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_ValidRequest_ShouldReturnOk() throws Exception {
        UserCreateRequest request = new UserCreateRequest("new_admin@gamail.com", "F", "I", "O",  Role.ADMIN, "111111");
        UserResponse response = new UserResponse(2L, "new_admin@gamail.com", "F I O");

        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_AsAdmin_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/user/delete/{id}", 10L))
                .andExpect(status().isOk());

        verify(userService).deleteUser(10L);
    }
}