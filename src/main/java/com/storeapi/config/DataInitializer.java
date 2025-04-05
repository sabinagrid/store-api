package com.storeapi.config;

import com.storeapi.entity.User;
import com.storeapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers() {
        return args -> {
            if (userRepository.findByEmail("sa").isEmpty()) {
                User user = new User();
                user.setEmail("sa");
                user.setPasswordHash(passwordEncoder.encode("sa"));
                userRepository.save(user);
            }
        };
    }
}
