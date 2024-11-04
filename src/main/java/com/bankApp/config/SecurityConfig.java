package com.bankApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll() // Allow public access to specified endpoints
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .csrf(csrf -> csrf
                        .disable() // Disable CSRF protection; be cautious with this
                );

        return http.build();
    }
}
