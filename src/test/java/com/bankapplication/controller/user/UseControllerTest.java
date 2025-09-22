package com.bankapplication.controller.user;

import com.bankapplication.dto.ResetPasswordRequestDto;
import com.bankapplication.exception.TokenExpiredException;
import com.bankapplication.service.JwtService;
import com.bankapplication.service.MyUserDetailsService;
import com.bankapplication.service.UserRateLimiterService;
import com.bankapplication.service.userService.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UseController.class)
@AutoConfigureMockMvc(addFilters = false)
class UseControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private UserService userService;
    @MockBean private JwtService jwtService;
    @MockBean private MyUserDetailsService userDetailsService;
    @MockBean private UserRateLimiterService rateLimiterService;

    @Test
    @WithMockUser(username = "user@gmail.com", authorities = {"ROLE_CUSTOMER"})
    void forgotPassword_shouldReturnSuccess_whenEmailIsValid() throws Exception {
        String email = "user@gmail.com";

        // No need to stub return value since it returns void
        doNothing().when(userService).createPasswordResetToken(email);

        mockMvc.perform(post("/api/v1/user/forgot-password")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset link sent"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", authorities = {"ROLE_CUSTOMER"})
    void verifyResetToken_shouldReturnUnauthorized_whenTokenIsExpired() throws Exception {
        String expiredToken = "expiredOrInvalidToken";

        doThrow(new TokenExpiredException("Token has expired"))
                .when(userService).verifyPasswordResetToken(expiredToken);

        mockMvc.perform(post("/api/v1/user/verify-reset-token")
                        .param("token", expiredToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Token has expired"))
                .andExpect(jsonPath("$.status").value(401));
    }


    @Test
    @WithMockUser(username = "user@gmail.com", authorities = {"ROLE_CUSTOMER"})
    void resetPassword_shouldReturnSuccess_whenInputIsValid() throws Exception {
        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setOtp("123456");
        request.setNewPassword("NewPass123");
        request.setConfirmPassword("NewPass123");

        String json = """
        {
          "otp": "123456",
          "newPassword": "NewPass123",
          "confirmPassword": "NewPass123"
        }
    """;

        mockMvc.perform(post("/api/v1/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .principal(() -> "user@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password has been successfully reset"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", authorities = {"ROLE_CUSTOMER"})
    void uploadImage_shouldReturnSuccess_whenImageIsValid() throws Exception {
        Long userId = 1L;
        String expectedUrl = "https://cdn.example.com/user-images/1.jpg";

        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy image content".getBytes()
        );

        when(userService.uploadUserImage(eq(userId), any(MultipartFile.class)))
                .thenReturn(expectedUrl);

        mockMvc.perform(multipart("/api/v1/user/{userId}/upload-image", userId)
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("POST"); // multipart defaults to GET in Spring, override it
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Image uploaded successfully"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").value(expectedUrl));
    }


}