package com.bankapplication.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
@Component
public class ResetPasswordOtp {


    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String CHAR_POOL = LETTERS + DIGITS;
    private static final int TOKEN_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private ResetPasswordOtp() {
        // private constructor to prevent instantiation
    }

    public  String generateResetToken() {
        String token;
        do {
            StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
            for (int i = 0; i < TOKEN_LENGTH; i++) {
                sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
            }
            token = sb.toString();
        } while (!isValid(token));
        return token;
    }

    private static boolean isValid(String token) {
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : token.toCharArray()) {
            if (LETTERS.indexOf(c) >= 0) hasLetter = true;
            if (DIGITS.indexOf(c) >= 0) hasDigit = true;
            if (hasLetter && hasDigit) return true;
        }
        return false;
    }
}
