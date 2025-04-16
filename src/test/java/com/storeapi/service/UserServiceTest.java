package com.storeapi.service;

import com.storeapi.dto.LoginRequest;
import com.storeapi.dto.RegisterRequest;
import com.storeapi.entity.User;
import com.storeapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldReturnTrue_whenEmailNotExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("secret");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");

        boolean result = userService.register(request);

        assertTrue(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldReturnFalse_whenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.register(request);

        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnSessionId_whenCredentialsAreValid() {
        String email = "test@example.com";
        String rawPassword = "secret";
        String encodedPassword = "encoded";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        User user = new User(1L, email, encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        Optional<String> sessionId = userService.login(request);

        assertTrue(sessionId.isPresent());
    }

    @Test
    void login_shouldReturnEmpty_whenInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@example.com");
        request.setPassword("wrong");

        when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        Optional<String> sessionId = userService.login(request);

        assertTrue(sessionId.isEmpty());
    }
}
