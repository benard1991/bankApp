package com.bankApp.controller;

import com.bankApp.dto.LoginRequest;
import com.bankApp.model.User;
import com.bankApp.security.JwtUtil;
import com.bankApp.security.PasswordEncoderUtil;
import com.bankApp.services.CustomUserDetailsService;
import com.bankApp.services.UserService;
import com.bankApp.dto.CustomResponse;
import com.bankApp.dto.TokenRefreshRequest;
import com.bankApp.dto.UserLoginResponse;
import com.bankApp.util.ValidationErrorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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


    @PostMapping("/refresh")
    public ResponseEntity<CustomResponse> refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
        String refreshToken = tokenRefreshRequest.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResponse<>(HttpStatus.BAD_REQUEST.value(), "Refresh token is required"));
        }
        try {
            User user = userService.findByRefreshToken(refreshToken);
            if (user == null) {
                logger.warn("No user found for refresh token: {}", refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "User with token " + refreshToken + " not found"));
            }

            if (jwtUtil.isTokenExpired(refreshToken)) {
                logger.warn("Refresh token has expired: {}", refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "Refresh token has expired"));
            }

            if (jwtUtil.validateToken(refreshToken, user.getEmail())) {
                // Generate new access and refresh tokens
                String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
                String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

                user.setRefreshToken(newRefreshToken);
                user.setRefreshTokenExpirationTime(System.currentTimeMillis() + jwtUtil.getRefreshTokenExpirationTime()); // Update expiration time
                userService.save(user);

                long newAccessTokenExpirationTime = System.currentTimeMillis() + jwtUtil.getAccessTokenExpirationTime();
                long newRefreshTokenExpirationTime = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpirationTime();

                // Format the expiration times into a human-readable date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String newAccessTokenExpirationDate = dateFormat.format(new Date(newAccessTokenExpirationTime));
                String newRefreshTokenExpirationDate = dateFormat.format(new Date(newRefreshTokenExpirationTime));

                UserLoginResponse loginResponse = new UserLoginResponse(
                        user.getEmail(),
                        user.getRole(),
                        newAccessToken,
                        newRefreshToken,
                        newAccessTokenExpirationDate,
                        newRefreshTokenExpirationDate
                );

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new CustomResponse<>(loginResponse, HttpStatus.OK.value(), "Token refreshed successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired refresh token"));
            }
        } catch (Exception e) {
            logger.error("Error refreshing token for refresh token: {}", refreshToken, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error refreshing token"));
        }
    }


    @GetMapping("/logout")
    public ResponseEntity<CustomResponse> logout(HttpServletRequest request) {

        String token = jwtUtil.extractJwtFromRequest(request);
        logger.error("token: {}", token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResponse<>(HttpStatus.BAD_REQUEST.value(), "Token is missing or invalid"));
        }
        try {

            // Extract the username (email) from the token
            String username = jwtUtil.extractUsername(token);
            logger.error("user: {}", username);

            if (!jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token"));
            }

            Optional<User> userOptional = userService.findByEmail(username);

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "User not found"));
            }

            User user = userOptional.get();
            user.setRefreshToken(null);
            userService.save(user);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new CustomResponse<>(HttpStatus.OK.value(), "Logout successful"));
        } catch (Exception e) {
            logger.error("Error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error during logout"));
        }
    }


}


