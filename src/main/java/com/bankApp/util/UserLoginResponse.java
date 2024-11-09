package com.bankApp.util;

import lombok.Data;

@Data
public class UserLoginResponse {
        private String email;
        private String role;
        private String accessToken;
        private String refreshToken;
        private String accessTokenExpirationDate;
        private String refreshTokenExpirationDate;

        public UserLoginResponse(String email, String role, String accessToken, String refreshToken,
                                 String accessTokenExpirationDate, String refreshTokenExpirationDate) {
            this.email = email;
            this.role = role;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.accessTokenExpirationDate = accessTokenExpirationDate;
            this.refreshTokenExpirationDate = refreshTokenExpirationDate;
        }

        // Getters and setters
    }


