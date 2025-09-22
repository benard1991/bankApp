package com.bankapplication.repository;

import com.bankapplication.model.Token;
import com.bankapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByAccessTokenAndStatus(String accessToken, String status);
    // Find a token by its access token and its status (e.g., "ACTIVE", "REVOKED")

    // Find all tokens associated with a specific user
    List<Token> findByUserId(Long userId);

    // Find the active token for a user
    Optional<Token> findByUserIdAndStatus(Long userId, String status);

    // Find a token by its refresh token and its status
    Optional<Token> findByRefreshTokenAndStatus(String refreshToken, String status);

    // Find all tokens that are expired (you may need to add expiration date to the query)
    List<Token> findByAccessTokenExpirationBeforeAndStatus(LocalDateTime expirationTime, String status);

    // Find a token by its access token, user and status
    Optional<Token> findByAccessTokenAndUserIdAndStatus(String accessToken, Long userId, String status);

    void deleteAllByUser(User user);

    // Find all tokens for a user (active or revoked)
}
