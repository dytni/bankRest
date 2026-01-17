package com.example.bankcards.controller;

import com.example.bankcards.BaseControllerTest;
import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.auth.request.SignInRequest;
import com.example.bankcards.dto.auth.request.SignUpRequest;
import com.example.bankcards.dto.auth.response.JwtResponse;
import com.example.bankcards.dto.user.response.UserResponse;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest extends BaseControllerTest {



    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;



    @Test
    void authenticateUser_ShouldReturnOk() throws Exception {
        SignInRequest request = new SignInRequest("testuser@gmail.com", "password123");

        JwtResponse response = new JwtResponse("access_token", "refresh_token", "Bearer");

        when(authService.signInAndGenerateTokens(any(SignInRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token"));
    }

    @Test
    void registerUser_ShouldReturnCreated() throws Exception {
        UserResponse response = new UserResponse(1L, "testuser@gmail.com", "Ivanov Ivan Ivanovich");

        when(userService.createUser((SignUpRequest) any())).thenReturn(response);

        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser@gmail.com\", \"password\":\"123456\", \"firstName\":\"Ivan\", \"lastName\":\"Ivanov\", \"secondName\":\"Ivanovich\", \"confirmPassword\":\"123456\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser@gmail.com"));
    }
}