package com.bankapplication.service.tokenService;
import com.bankapplication.exception.TokenExpiredException;
import com.bankapplication.model.PasswordResetToken;
import com.bankapplication.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenServiceImpl implements TokenService {
    private  final PasswordResetTokenRepository passwordResetTokenRepository;

    public TokenServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public PasswordResetToken validateResetToken(String token){
        return passwordResetTokenRepository.findByOtp(token)
                .filter(t -> t.getExpiryTime().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new TokenExpiredException("Invalid or expired OTP token."));
    }
}
