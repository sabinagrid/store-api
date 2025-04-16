package com.storeapi.security;

import com.storeapi.entity.User;
import com.storeapi.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessionAuthFilterTest {

    @Test
    void doFilter_shouldSetAuthentication_whenSessionIsValid() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("session@test.com");

        UserService userService = Mockito.mock(UserService.class);
        Mockito.when(userService.getUserBySession("valid-session")).thenReturn(Optional.of(user));

        SessionAuthFilter filter = new SessionAuthFilter(userService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "valid-session");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (ServletRequest req, ServletResponse res) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            assertAll(
                    () -> assertNotNull(auth, "Authentication should not be null"),
                    () -> assertInstanceOf(User.class, auth.getPrincipal(), "Principal should be instance of User"),
                    () -> assertEquals(user.getEmail(), ((User) auth.getPrincipal()).getEmail(), "Email should match")
            );
        };

        filter.doFilter(request, response, chain);
    }
}
