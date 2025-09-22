package com.bankapplication.service;

import com.bankapplication.exception.TokenExpiredException;
import com.bankapplication.exception.TokenNotFoundException;
import com.bankapplication.model.Token;
import com.bankapplication.model.User;
import com.bankapplication.repository.TokenRepository;
import com.bankapplication.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class JwtService {

    private final String secretKey;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            TokenRepository tokenRepository,
            UserRepository userRepository) {
        this.secretKey = secretKey;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> generateAccessAndRefreshTokens(UserDetails userDetails) {
        User user = (User) userDetails;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpiration = now.plusHours(1);
        LocalDateTime refreshTokenExpiration = now.plusDays(7);

        Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date accessExpDate = Date.from(accessTokenExpiration.atZone(ZoneId.systemDefault()).toInstant());
        Date refreshExpDate = Date.from(refreshTokenExpiration.atZone(ZoneId.systemDefault()).toInstant());

        String accessToken = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", getRolesFromUser(user))
                .setIssuedAt(issuedAt)
                .setExpiration(accessExpDate)
                .signWith(getKey())
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(refreshExpDate)
                .signWith(getKey())
                .compact();

        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setAccessTokenExpiration(accessTokenExpiration);
        token.setRefreshTokenExpiration(refreshTokenExpiration);
        token.setIssuedAt(now);
        token.setTokenType("ACCESS_REFRESH");
        token.setStatus("ACTIVE");
        token.setUser(user);

        tokenRepository.save(token);

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("accessTokenExpiration", accessTokenExpiration.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        tokens.put("refreshToken", refreshToken);
        tokens.put("refreshTokenExpiration", refreshTokenExpiration.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return tokens;
    }


    private List<String> getRolesFromUser(User user) {
        List<String> roles = new ArrayList<>();
        user.getAuthorities().forEach(authority -> roles.add(authority.getAuthority()));
        return roles;
    }


    public boolean validateToken(String token, UserDetails user) {
        try {
            Claims claims = extractAllClaims(token);

            if (claims.getExpiration().before(new Date())) {
                throw new TokenExpiredException("===>Token has expired.");
            }

            Optional<Token> tokenRecord = tokenRepository.findByAccessTokenAndStatus(token, "ACTIVE");
            if (tokenRecord.isEmpty()) {
                throw new TokenNotFoundException("Token is either expired or revoked.");
            }

            String username = claims.getSubject();
            Optional<User> userRecord = userRepository.findByUsername(username);
            if (userRecord.isEmpty()) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

            return true;
        } catch (TokenExpiredException | TokenNotFoundException | UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during token validation: " + e.getMessage(), e);
        }
    }


    public boolean hasRequiredRole(String token, String requiredRole) {
        List<String> roles = getRolesFromToken(token);
        return roles.contains(requiredRole);
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return (List<String>) claims.get("roles");
    }


    public String extractUsername(String token) {
        String username = extractAllClaims(token).getSubject();
        Optional<Token> tokenRecord = tokenRepository.findByAccessTokenAndStatus(token, "ACTIVE");
        if (tokenRecord.isEmpty()) {
            throw new IllegalStateException("Token is either expired or revoked.");
        }
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return username;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
