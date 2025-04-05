package com.storeapi.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        String rawPassword = "admin";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
    }
}
