package com.bankapplication.config;

import com.bankapplication.exception.GlobalExceptionHandler.ErrorDetails;
import com.bankapplication.service.JwtService;
import com.bankapplication.service.MyUserDetailsService;
import com.bankapplication.service.UserRateLimiterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserRateLimiterService rateLimiterService;

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            token = authHeader.substring(TOKEN_PREFIX.length());
            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                logger.info("Token has expired o......");
                respondWithError(response, 401, "Token has expired", "UNAUTHORIZED");
                return;

            }
        }
        if (username != null) {
            //Apply Rate Limiting
            Bucket bucket = rateLimiterService.resolveBucket(username);
            if (!bucket.tryConsume(1)) {
                logger.info("Too many requests o......");
                respondWithError(response, 429, "Too many requests. Please try again later.", "RATE_LIMIT_EXCEEDED");
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }


    private void respondWithError(HttpServletResponse response, int status, String message, String code)
            throws IOException {
        ErrorDetails error = new ErrorDetails(status, message, code);
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
