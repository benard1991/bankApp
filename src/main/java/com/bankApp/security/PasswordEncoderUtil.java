package com.bankApp.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordEncoderUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Returns the PasswordEncoder Bean
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }

    // Encode the plain password
    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    // Check if the plain password matches the encoded password
    public boolean matches(String plainPassword, String encodedPassword) {
        return passwordEncoder.matches(plainPassword, encodedPassword);
    }
}
