package com.example.bankcards;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.security.JwtCore;
import com.example.bankcards.security.TokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;


@WebMvcTest
@Import({SecurityConfig.class, JwtCore.class})
public abstract class BaseControllerTest {
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected MockMvc mockMvc;
    @MockitoBean protected JwtCore jwtCore;

    @MockitoBean protected TokenFilter tokenFilter;
    @MockitoBean protected UserDetailsService userDetailsService;

    @BeforeEach
    void setup() throws Exception {
        doAnswer(invocation -> {
            var request = invocation.getArgument(0, jakarta.servlet.http.HttpServletRequest.class);
            var response = invocation.getArgument(1, jakarta.servlet.http.HttpServletResponse.class);
            var filterChain = invocation.getArgument(2, jakarta.servlet.FilterChain.class);
            filterChain.doFilter(request, response);
            return null;
        }).when(tokenFilter).doFilter(any(), any(), any());
    }
}