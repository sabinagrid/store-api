package com.storeapi.service;

import com.storeapi.dto.LoginRequest;
import com.storeapi.dto.RegisterRequest;
import com.storeapi.entity.User;
import com.storeapi.repository.UserRepository;
import com.storeapi.session.SessionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, SessionInfo> sessions = new HashMap<>();
    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private final Map<String, LocalDateTime> lockedUntil = new HashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;
    private static final int SESSION_EXPIRY_HOURS = 1;

    public boolean register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            log.warn("Registration attempt failed: Email already exists - {}", req.getEmail());
            return false;
        }
        userRepository.save(new User(null, req.getEmail(), passwordEncoder.encode(req.getPassword())));
        log.info("User registered successfully: {}", req.getEmail());
        return true;
    }

    public Optional<String> login(LoginRequest req) {
        String email = req.getEmail();

        if (isLocked(email)) {
            log.warn("Login blocked due to too many failed attempts: {}", email);
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Account locked. Try again later.");
        }

        loginAttempts.putIfAbsent(email, 0);

        Optional<String> session = userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
                .map(user -> {
                    String sessionId = UUID.randomUUID().toString();
                    sessions.put(sessionId, new SessionInfo(user, LocalDateTime.now()));
                    loginAttempts.put(email, 0);
                    lockedUntil.remove(email);
                    log.info("User logged in successfully: {}", email);
                    return sessionId;
                });

        if (session.isEmpty()) {
            int attempts = loginAttempts.get(email) + 1;
            loginAttempts.put(email, attempts);
            log.warn("Failed login attempt {} for user {}", attempts, email);

            if (attempts >= MAX_ATTEMPTS) {
                lockedUntil.put(email, LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                throw new ResponseStatusException(TOO_MANY_REQUESTS, "Too many failed login attempts. Account locked for 15 minutes.");
            }
        }

        return session;
    }

    public Optional<User> getUserBySession(String sessionId) {
        SessionInfo sessionInfo = sessions.get(sessionId);
        if (sessionInfo == null) {
            return Optional.empty();
        }
        if (isSessionExpired(sessionInfo)) {
            sessions.remove(sessionId);
            log.info("Session expired for sessionId: {}", sessionId);
            return Optional.empty();
        }
        return Optional.of(sessionInfo.getUser());
    }

    public boolean resetPassword(String email, String newPassword) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    log.info("Password reset successfully for {}", email);
                    return true;
                })
                .orElse(false);
    }

    private boolean isLocked(String email) {
        LocalDateTime unlockTime = lockedUntil.get(email);
        if (unlockTime == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(unlockTime)) {
            lockedUntil.remove(email);
            loginAttempts.put(email, 0);
            return false;
        }
        return true;
    }

    private boolean isSessionExpired(SessionInfo sessionInfo) {
        return sessionInfo.getCreatedAt().plusHours(SESSION_EXPIRY_HOURS).isBefore(LocalDateTime.now());
    }
}
