package com.bankApp.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;  // Utility to generate, extract and validate JWT

    @Autowired
    private AuthenticationManager authenticationManager; // For authentication

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extract JWT from the request
        String jwtToken = extractJwtFromRequest(request);

        // If there's a JWT and it's valid, authenticate the user
        if (StringUtils.hasText(jwtToken) && jwtUtil.validateToken(jwtToken, jwtUtil.extractUsername(jwtToken))) {
            // Extract username from JWT
            String username = jwtUtil.extractUsername(jwtToken);

            // Extract roles (authorities) from the JWT
            List<SimpleGrantedAuthority> authorities = extractAuthoritiesFromToken(jwtToken);

            // Create an authentication token with authorities
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);

            // Set the authentication in the Security Context
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Continue with the filter chain (proceed to the next filter or the actual endpoint)
        filterChain.doFilter(request, response);
    }

    // Helper method to extract JWT from the request (can be from headers or cookies)
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Extract JWT token by removing "Bearer " prefix
            return bearerToken.substring(7);
        }
        return null;
    }

    // Helper method to extract authorities (roles) from the JWT
    private List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        try {
            // Extract roles from the JWT claims
            // Assuming roles are stored under the claim "roles" in the token
            List<String> roles = (List<String>) Jwts.parser()
                    .setSigningKey(jwtUtil.getSecretKey())  // Get secret key from the JwtUtil
                    .parseClaimsJws(token)
                    .getBody()
                    .get("roles");  // Retrieve roles claim

            // Map roles to SimpleGrantedAuthority objects
            if (roles != null) {
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                }
            }
        } catch (Exception e) {
            // Handle invalid token or missing roles
            e.printStackTrace();
        }
        return authorities;
    }
}
