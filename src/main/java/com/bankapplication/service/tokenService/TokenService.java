package com.bankapplication.service.tokenService;

import com.bankapplication.model.PasswordResetToken;

public interface TokenService {
    PasswordResetToken validateResetToken(String token);
}
