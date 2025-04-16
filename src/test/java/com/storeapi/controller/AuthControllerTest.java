package com.storeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storeapi.dto.LoginRequest;
import com.storeapi.dto.RegisterRequest;
import com.storeapi.dto.ResetPasswordRequest;
import com.storeapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldReturn200_whenSuccessful() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@email.com");
        request.setPassword("123");

        Mockito.when(userService.register(any())).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void register_shouldReturn409_whenEmailExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@email.com");

        Mockito.when(userService.register(any())).thenReturn(false);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_shouldReturn200_whenCredentialsValid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@email.com");
        request.setPassword("123");

        Mockito.when(userService.login(any())).thenReturn(Optional.of("session123"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session123"));
    }

    @Test
    void login_shouldReturn401_whenInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@email.com");
        request.setPassword("wrong");

        Mockito.when(userService.login(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void resetPassword_shouldReturn200_whenSuccessful() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@email.com");
        request.setNewPassword("newpass");

        Mockito.when(userService.resetPassword(any(), any())).thenReturn(true);

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void resetPassword_shouldReturn404_whenUserNotFound() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("missing@email.com");

        Mockito.when(userService.resetPassword(any(), any())).thenReturn(false);

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
