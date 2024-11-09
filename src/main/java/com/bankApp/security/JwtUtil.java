package com.bankApp.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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


    public String getSecretKey() {
        return secretKey;
    }

    public String generateAccessToken(String username) {
        return createToken(username, accessTokenExpirationTime, new HashMap<>());
    }

    // Method to generate the JWT access token
    public String generateAccessToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);  // Add role as a claim
        return createToken(username, accessTokenExpirationTime, claims);
    }


    // Method to generate the refresh token
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
    public boolean isTokenExpired(String token) {
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

    // Extract the role from the JWT
    public String extractRole(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);  // Extract the role from the token
        } catch (Exception e) {
            return null; // Handle invalid token case
        }
    }

    public long getAccessTokenExpirationTime() {
        return accessTokenExpirationTime;
    }

    public long getRefreshTokenExpirationTime() {
        return refreshTokenExpirationTime;
    }


    public String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Extract the JWT token by removing the "Bearer " prefix
            return bearerToken.substring(7);
        }
        return null;
    }

}