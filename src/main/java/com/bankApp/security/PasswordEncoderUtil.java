package com.bankApp.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class PasswordEncoderUtil {
        private  final BCryptPasswordEncoder passwordEncoder;

        public PasswordEncoderUtil(){
            this.passwordEncoder = new BCryptPasswordEncoder();

        }
        public String encodePassword(String plainPassword) {
            return passwordEncoder.encode(plainPassword);
        }

        public boolean matches(String plainPassword, String encodedPassword) {
            return passwordEncoder.matches(plainPassword, encodedPassword);
        }
    }

