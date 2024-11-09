package com.bankApp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TokenRefreshRequest {

    @NotNull(message = "Refresh token is required")
    private String refreshToken;

}
