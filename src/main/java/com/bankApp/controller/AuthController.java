package com.bankApp.controller;

import com.bankApp.dto.LoginRequest;
import com.bankApp.model.User;
import com.bankApp.security.JwtUtil;
import com.bankApp.security.PasswordEncoderUtil;
import com.bankApp.services.CustomUserDetailsService;
import com.bankApp.services.UserService;
import com.bankApp.util.CustomResponse;
import com.bankApp.util.UserLoginResponse;
import com.bankApp.util.ValidationErrorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ValidationErrorService validationErrorService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private UserService userService;

        /**
         * Login endpoint to authenticate the user and generate JWT tokens.
         *
         * @param loginRequest Login request containing email and password
         * @param bindingResult Captures validation errors
         * @return ResponseEntity with access token and refresh token or error message
         */
        @PostMapping("/login")
        public ResponseEntity<CustomResponse> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
            logger.info("User login attempt for email: {}", loginRequest.getEmail());

            // Return validation errors if present
            if (bindingResult.hasErrors()) {
                CustomResponse<List<String>> errorResponse = validationErrorService.getErrorResponse(bindingResult);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            try {
                // Authenticate the user with the provided credentials
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
                authenticationManager.authenticate(authenticationToken);

                // Load the user and validate the password
                User user = userService.findByEmail(loginRequest.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // If password matches, generate JWT tokens
                if (passwordEncoderUtil.matches(loginRequest.getPassword(), user.getPassword())) {
                    String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
                    String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

                    logger.info("Generated Access Token: {}", accessToken);
                    logger.info("Generated Refresh Token: {}", refreshToken);

                    // Save the refresh token for future use (optional: store in DB or cache)
                    user.setRefreshToken(refreshToken);
                    userService.save(user);

                    // Calculate token expiration times
                    long accessTokenExpiration = System.currentTimeMillis() + jwtUtil.getAccessTokenExpirationTime();
                    long refreshTokenExpiration = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpirationTime();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String accessTokenExpirationDate = dateFormat.format(new Date(accessTokenExpiration));
                    String refreshTokenExpirationDate = dateFormat.format(new Date(refreshTokenExpiration));

                    UserLoginResponse loginResponse = new UserLoginResponse(
                            user.getEmail(),
                            user.getRole(),
                            accessToken,
                            refreshToken,
                            accessTokenExpirationDate,
                            refreshTokenExpirationDate
                    );
                    // Return the generated tokens and expiration dates
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new CustomResponse<>( loginResponse,HttpStatus.OK.value(), "Login successful"));
                } else {
                    // Password mismatch
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials"));
                }

            } catch (Exception e) {
                logger.error("Login failed for email: {}", loginRequest.getEmail(), e);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials"));
            }
        }
    }


