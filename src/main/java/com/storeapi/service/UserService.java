package com.storeapi.service;

import com.storeapi.dto.LoginRequest;
import com.storeapi.dto.RegisterRequest;
import com.storeapi.entity.User;
import com.storeapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, User> sessions = new HashMap<>();
    private final Map<String, Integer> loginAttempts = new HashMap<>();

    public boolean register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) return false;
        userRepository.save(new User(null, req.getEmail(), passwordEncoder.encode(req.getPassword())));
        return true;
    }

    public Optional<String> login(LoginRequest req) {
        String email = req.getEmail();
        loginAttempts.putIfAbsent(email, 0);
        if (loginAttempts.get(email) >= 5) throw new RuntimeException("Too many attempts");

        Optional<String> session = userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
                .map(user -> {
                    String sessionId = UUID.randomUUID().toString();
                    sessions.put(sessionId, user);
                    loginAttempts.put(email, 0);
                    return sessionId;
                });

        if (session.isEmpty()) {
            loginAttempts.put(email, loginAttempts.get(email) + 1);
        }

        return session;
    }

    public Optional<User> getUserBySession(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }
}
