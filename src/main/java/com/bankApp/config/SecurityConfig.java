package com.bankApp.config;
import com.bankApp.security.PasswordEncoderUtil;
import com.bankApp.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoderUtil passwordEncoderUtil) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoderUtil = passwordEncoderUtil;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // Ensure that the correct type is passed
        authenticationManagerBuilder
                .userDetailsService(userDetailsService) // Pass the correct userDetailsService
                .passwordEncoder(passwordEncoderUtil.passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authz -> authz
                        .requestMatchers("/api/register", "/api/auth/login", "/api/auth/refresh","/api/auth/logout")
                        .permitAll()
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")  // Only users with ROLE_ADMIN can access /api/admin/**
                        .requestMatchers("/api/user/**")
                        .hasRole("USER")  // Only users with ROLE_USER can access /api/user/**
                        .anyRequest()
                        .authenticated()
                )
                .csrf(csrf -> csrf
                        .disable()
                )
                .authenticationManager(authenticationManager(http));

        return http.build();
    }
}
