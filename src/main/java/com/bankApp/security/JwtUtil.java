package com.bankApp.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationTime;

    public String generateAccessToken(String username) {
        return createToken(username, accessTokenExpirationTime, new HashMap<>());
    }

    public String generateRefreshToken(String username) {
        return createToken(username, refreshTokenExpirationTime, new HashMap<>());
    }

    private String createToken(String username, long expirationTime, Map<String, Object> additionalClaims) {
        return Jwts.builder()
                .setClaims(additionalClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            // Handle invalid token case (e.g., log the error or rethrow)
            return null;
        }
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return (extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token));
    }
    private boolean isTokenExpired(String token) {
        try {
            Date expirationDate = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expirationDate.before(new Date());
        } catch (Exception e) {
            // Handle exception for expired token or parsing error
            return true;
        }
    }

    public long getAccessTokenExpirationTime() {
        return accessTokenExpirationTime;
    }

    public long getRefreshTokenExpirationTime() {
        return refreshTokenExpirationTime;
    }
}