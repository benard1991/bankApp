package com.bankapplication.controller.user;

import com.bankapplication.dto.ChangePasswordRequest;
import com.bankapplication.dto.GenericResponse;
import com.bankapplication.exception.PasswordChangeException;
import com.bankapplication.exception.TokenExpiredException;
import com.bankapplication.exception.UserNotFoundException;
import com.bankapplication.model.User;
import com.bankapplication.service.JwtService;
import com.bankapplication.service.MyUserDetailsService;
import com.bankapplication.service.UserRateLimiterService;
import com.bankapplication.service.userService.UserService;
import com.bankapplication.service.userService.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProfileContoller.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileContollerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private UserService userService;
    @MockBean private JwtService jwtService;
    @MockBean private MyUserDetailsService userDetailsService;
    @MockBean private UserRateLimiterService rateLimiterService;

    @WithMockUser(authorities = {"ROLE_CUSTOMER"})
    @Test
    void getProfile_shouldReturnUserProfile_whenUserExists() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("benard@gmail.com");

        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/v1/profile/getProfile/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User found successfully"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.username").value("benard@gmail.com"));
    }


    @Test
    @WithMockUser(authorities = {"ROLE_CUSTOMER"})
    void getProfile_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        Long invalidUserId = 999L;

        when(userService.getUserById(invalidUserId))
                .thenThrow(new UserNotFoundException("User not found with ID: " + invalidUserId));

        mockMvc.perform(get("/api/v1/profile/getProfile/{id}", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: " + invalidUserId))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_CUSTOMER"})
    void changePassword_shouldReturnSuccessResponse_whenRequestIsValid() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPass123");
        request.setNewPassword("newPass456");
        request.setConfirmPassword("newPass456");

        GenericResponse<String> mockResponse = new GenericResponse<>(
                null,
                "Password changed successfully",
                HttpStatus.OK.value()
        );

        when(userService.changePassword(any(ChangePasswordRequest.class), eq("user@example.com")))
                .thenReturn(mockResponse);

        String requestJson = """
        {
          "currentPassword": "oldPass123",
          "newPassword": "newPass456",
          "confirmPassword": "newPass456"
        }
    """;

        // Act + Assert
        mockMvc.perform(post("/api/v1/profile/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .principal(() -> "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", authorities = {"ROLE_CUSTOMER"})
    void changePassword_shouldReturnBadRequest_whenCurrentPasswordIsWrong() throws Exception {
        String requestJson = """
        {
          "currentPassword": "wrongPass123",
          "newPassword": "newPass456",
          "confirmPassword": "newPass456"
        }
    """;

        when(userService.changePassword(any(ChangePasswordRequest.class), eq("user@gmail.com")))
                .thenThrow(new PasswordChangeException("Invalid current password"));

        mockMvc.perform(post("/api/v1/profile/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .principal(() -> "user@gmail.com"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Invalid current password"))
                .andExpect(jsonPath("$.status").value(403));

    }

    @Test
    @WithMockUser(username = "user@gmail.com", authorities = {"ROLE_CUSTOMER"})
    void deleteUser_shouldReturnSuccess_whenUserIsDeleted() throws Exception {
        Long userId = 1L;

        GenericResponse<String> mockResponse = new GenericResponse<>(
                null,
                "Account deleted successfully",
                HttpStatus.OK.value()
        );

        when(userService.deleteUser(userId)).thenReturn(mockResponse);

        mockMvc.perform(delete("/api/v1/profile/deleteAccount/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted successfully"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", authorities = {"ROLE_CUSTOMER"})
    void verifyResetToken_shouldReturnBadRequest_whenTokenIsInvalid() throws Exception {
        String token = "expiredOrInvalidToken";

        doThrow(new TokenExpiredException("Invalid or expired token"))
                .when(userService).verifyPasswordResetToken(token);

        mockMvc.perform(post("/api/v1/user/verify-reset-token")
                        .param("token", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"))
                .andExpect(jsonPath("$.status").value(401));
    }

}