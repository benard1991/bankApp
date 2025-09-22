package com.bankapplication.service.userService;

import com.bankapplication.dto.ChangePasswordRequest;
import com.bankapplication.dto.GenericResponse;
import com.bankapplication.dto.UpdateAccountRequest;
import com.bankapplication.dto.UserDto;
import com.bankapplication.exception.*;
import com.bankapplication.model.Account;
import com.bankapplication.model.PasswordResetToken;
import com.bankapplication.model.Role;
import com.bankapplication.model.User;
import com.bankapplication.model.enums.UserRole;
import com.bankapplication.repository.AccountRepository;
import com.bankapplication.repository.PasswordResetTokenRepository;
import com.bankapplication.repository.TokenRepository;
import com.bankapplication.repository.UserRepository;
import com.bankapplication.service.JwtService;
import com.bankapplication.service.mailService.EmailServiceImpl;
import com.bankapplication.service.roleService.RoleService;
import com.bankapplication.service.tokenService.TokenServiceImpl;
import com.bankapplication.util.ResetPasswordOtp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private EmailServiceImpl emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenServiceImpl tokenService;
    @Mock
    private ResetPasswordOtp resetPasswordOtp;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager  authenticationManager;
    @Mock private RoleService roleService;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;


    @Test
    void createUser_shouldSucceed_WhenValidCustomerRequest() {
        UserDto userDto = new UserDto();
        userDto.setUsername("ben@example.com");
        userDto.setFirstname("John");
        userDto.setLastname("Doe");
        userDto.setAge(30);
        userDto.setPassword("password123");
        userDto.setPhoneNumber("08012345678");
        userDto.setNationality("Nigerian");
        userDto.setReligion("Christian");
        userDto.setAccountType("SAVINGS");
        userDto.setLocalGovernment("Eti-Osa");
        userDto.setBvn("12345678901");
        userDto.setNin("98765432101");
        userDto.setState("Lagos");
        userDto.setRole(List.of("CUSTOMER"));
        userDto.setNextOfKinFirstName("Jane");
        userDto.setNextOfKinLastName("Doe");
        userDto.setNextOfKinPhoneNumber("07098765432");
        userDto.setNextOfKinAddress("123 Street");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername(userDto.getUsername());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(accountRepository.findByUser(any(User.class))).thenReturn(new ArrayList<>());

        Role mockRole = new Role();
        mockRole.setName("CUSTOMER");
        when(roleService.getOrCreateRole(UserRole.CUSTOMER)).thenReturn(mockRole);

        User result = userService.createUser(userDto);
        System.out.println("Registration successful");
        assertNotNull(result);
        assertEquals("ben@example.com", result.getUsername());
        verify(emailService).sendRegistrationEmail(eq(userDto.getUsername()), eq(userDto.getFirstname()));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void createUser_shouldThrow_WhenUserAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setUsername("existing@example.com");
        userDto.setRole(List.of("CUSTOMER"));
        userDto.setNextOfKinFirstName("Jane");
        userDto.setNextOfKinLastName("Doe");
        userDto.setNextOfKinPhoneNumber("07098765432");

        User existingUser = new User();
        when(userRepository.findByUsername("existing@example.com")).thenReturn(Optional.of(existingUser));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(userDto);
        });
        System.out.println("Exception "+exception.getMessage());
        assertEquals("User with this username already exists", exception.getMessage());

        verify(userRepository).findByUsername("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnTokensAndUserInfo_WhenCredentialsAreValid() {
        String username = "test@example.com";
        String password = "password123";

        Role role = new Role();
        role.setName("CUSTOMER");

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setPhoneNumber("1234567890");
        user.setAge(30);
        user.setNationality("Nigerian");
        user.setReligion("Christianity");
        user.setAccountType("SAVINGS");
        user.setLocalGovernment("LGA");
        user.setBvn("12345678901");
        user.setNin("98765432109");
        user.setState("Lagos");
        user.setNextOfKinFirstName("Jane");
        user.setNextOfKinLastName("Doe");
        user.setNextOfKinPhoneNumber("0987654321");
        user.setNextOfKinAddress("123 Street");
        user.setRoles(Set.of(role));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        Map<String, Object> fakeTokens = new HashMap<>();
        fakeTokens.put("accessToken", "access-token");
        fakeTokens.put("refreshToken", "refresh-token");
        fakeTokens.put("accessTokenExpiration", "2025-01-01T00:00:00Z");
        fakeTokens.put("refreshTokenExpiration", "2025-01-02T00:00:00Z");

        when(jwtService.generateAccessAndRefreshTokens(user)).thenReturn(fakeTokens);

        Map<String, Object> result = userService.login(username, password);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateAccessAndRefreshTokens(user);
        verify(emailService).sendLoginNotification(username, "John");

        assertEquals("access-token", result.get("accessToken"));
        assertEquals("refresh-token", result.get("refreshToken"));

        Map<String, Object> userInfo = (Map<String, Object>) result.get("user");
        assertEquals("test@example.com", userInfo.get("username"));
        assertEquals("John", userInfo.get("firstname"));
        assertEquals("CUSTOMER", userInfo.get("role"));
    }

    @Test
    void login_shouldThrowException_WhenAuthenticationFails() {
        String username = "invalid@example.com";
        String password = "wrongPassword";

        when(authenticationManager.authenticate(any()))
                .thenThrow(new InvalideLoginCredentials("Bad credentials"));

        InvalideLoginCredentials exception = assertThrows(InvalideLoginCredentials.class, () -> {
            userService.login(username, password);
        });

        System.out.println("Exception caught: " + exception.getMessage());

        assertEquals("Invalid login credentails", exception.getMessage());
    }

    @Test
    void updateUser_shouldUpdateSuccessfully_WhenUserExistsAndIsNotCustomer() {
        Long userId = 1L;

        Role role = new Role();
        role.setName("ADMIN");

        User user = new User();
        user.setId(userId);
        user.setRoles(Set.of(role));

        UpdateAccountRequest request = new UpdateAccountRequest();
        request.setFirstname("Benard");
        request.setLastname("Ifeanyi");
        request.setAge(30);
        request.setPhoneNumber("09012345678");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updatedUser = userService.updateUser(userId, request);

        assertEquals("Benard", updatedUser.getFirstname());
        assertEquals("Ifeanyi", updatedUser.getLastname());
        assertEquals(30, updatedUser.getAge());
        assertEquals("09012345678", updatedUser.getPhoneNumber());

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldThrow_WhenUserNotFound() {
        Long userId = 4L;
        UpdateAccountRequest request = new UpdateAccountRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, request);
        });

        System.out.println("Exception caught: " + exception.getMessage());

        assertEquals("User not found with ID: 4", exception.getMessage());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_shouldThrow_WhenCustomerNextOfKinMissing() {
        Long userId = 3L;

        Role role = new Role();
        role.setName("CUSTOMER");

        User user = new User();
        user.setId(userId);
        user.setRoles(Set.of(role));

        UpdateAccountRequest request = new UpdateAccountRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        InvalidNextOfKinDetailsException exception = assertThrows(InvalidNextOfKinDetailsException.class, () -> {
            userService.updateUser(userId, request);
        });

        System.out.println("Exception message: " + exception.getMessage());

        assertEquals("Next of Kin details are required for the CUSTOMER role.", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test

    void changePassword_shouldSucceed_WhenValidInput() {
        String username = "test@example.com";
        String rawOldPassword = "oldPass123";
        String rawNewPassword = "newPass123";
        String encodedOldPassword = "encodedOldPass123";
        String encodedNewPassword = "encodedNewPass123";

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(rawOldPassword);
        request.setNewPassword(rawNewPassword);
        request.setConfirmPassword(rawNewPassword);

        User user = new User();
        user.setUsername(username);
        user.setFirstname("Benard");
        user.setPassword(encodedOldPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawOldPassword, encodedOldPassword)).thenReturn(true);
        when(passwordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GenericResponse response = userService.changePassword(request, username);

        assertNotNull(response);
        assertEquals("Password changed successfully", response.getMessage());
        assertEquals(200, response.getStatus());

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(rawOldPassword, encodedOldPassword);
        verify(passwordEncoder).encode(rawNewPassword);
        verify(userRepository).save(user);
        verify(emailService).sendPasswordChangeConfirmation(username, user.getFirstname());

        System.out.println("Password change success test passed!");
    }


    @Test
    void changePassword_shouldThrow_WhenUserNotFound() {
        String username = "notfound@example.com";
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("anyPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.changePassword(request, username);
        });
        System.out.println("Exception caught: " + exception.getMessage());
        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository, passwordEncoder, emailService);
    }

    @Test
    void changePassword_shouldThrow_WhenOldPasswordDoesNotMatch() {
        String username = "user@example.com";

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongOld");
        request.setNewPassword("new123");
        request.setConfirmPassword("new123");

        User user = new User();
        user.setUsername(username);
        user.setFirstname("Benard");
        user.setPassword("encodedCorrectOld");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOld", "encodedCorrectOld")).thenReturn(false);

        PasswordChangeException exception = assertThrows(PasswordChangeException.class, () -> {
            userService.changePassword(request, username);
        });

        verify(userRepository).findByUsername(username);

    }

    @Test
    void activateUser_shouldActivateInactiveUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("kate@example.com");
        user.setFirstname("Ben");
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        user.setActive(false);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.activateUser(userId);

        assertFalse(result.isActive());
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
        verify(emailService).sendAccountActivatedEmail(user.getUsername(), user.getFirstname());
        System.out.println("Test passed: activateUser_shouldActivateInactiveUser");

    }

    @Test
    void activateUser_shouldThrow_WhenUserNotFound() {
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.activateUser(userId);
        });

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository, emailService);

        System.out.println("Exception caught: " + exception.getMessage());

        assertEquals("User not found for ID: 99", exception.getMessage());

    }

    @Test
    void activateUser_shouldThrow_WhenUserAlreadyActive() {
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserAlreadyActiveException exception = assertThrows(UserAlreadyActiveException.class, () -> {
            userService.activateUser(userId);
        });

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository, emailService);

        System.out.println(exception.getMessage());

    }


    @Test
    void createPasswordResetToken_shouldSucceed_WhenUserExists() {
        String email = "benard@example.com";
        String otp = "123456";
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setFirstname("Benard");
        user.setUsername(email);

        when(userRepository.findByUsername(email)).thenReturn(Optional.of(user));
        when(resetPasswordOtp.generateResetToken()).thenReturn(otp);

        userService.createPasswordResetToken(email);

        verify(userRepository).findByUsername(email);
        verify(passwordResetTokenRepository).deleteByUser(user);
        verify(resetPasswordOtp).generateResetToken();
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());

        PasswordResetToken capturedToken = tokenCaptor.getValue();
        assertEquals(otp, capturedToken.getOtp());
        assertEquals(user, capturedToken.getUser());
        assertTrue(capturedToken.getExpiryTime().isAfter(now));

        verify(emailService).sendResetPasswordLink(email, otp, user.getFirstname());
    }

    @Test
    void createPasswordResetToken_shouldThrow_WhenUserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByUsername(email)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            userService.createPasswordResetToken(email);
        });

        System.out.println("Exception message: " + ex.getMessage());
        assertEquals("User not found", ex.getMessage());

        verify(userRepository).findByUsername(email);
        verifyNoMoreInteractions(passwordResetTokenRepository, emailService, resetPasswordOtp);
    }




    @Test
    void verifyPasswordResetToken_shouldCallTokenServiceWithCorrectToken() {
        String token = "3432-token";
        tokenService.validateResetToken(token);

        verify(tokenService, times(1)).validateResetToken(token);
    }


    @Test
    void verifyPasswordResetToken_shouldThrowException_WhenTokenIsInvalid() {
        String token = "invalid-token";

        doThrow(new TokenNotFoundException("Token is invalid"))
                .when(tokenService).validateResetToken(token);

        TokenNotFoundException exception = assertThrows(TokenNotFoundException.class, () -> {
            userService.verifyPasswordResetToken(token);
        });
        System.out.println("Exception message: " + exception.getMessage());

        assertEquals("Token is invalid", exception.getMessage());

        verify(tokenService).validateResetToken(token);
    }

    @Test
    void resetPassword_shouldSucceedWhenValidTokenAndMatchingPasswords() {
        String token = "valid-token";
        String newPassword = "newPass123";
        String confirmPassword = "newPass123";

        User mockUser = new User();
        mockUser.setUsername("test@example.com");
        mockUser.setFirstname("Benard");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(mockUser);

        when(tokenService.validateResetToken(token)).thenReturn(resetToken);
        when(passwordEncoder.encode(newPassword)).thenReturn("hashedPassword");

        userService.resetPassword(token, newPassword, confirmPassword);

        verify(userRepository).save(mockUser);
        verify(passwordResetTokenRepository).delete(resetToken);
        verify(emailService).sendPasswordChangeConfirmation("test@example.com", "Benard");

        assertEquals("hashedPassword", mockUser.getPassword());
    }

    @Test
    void resetPassword_shouldThrowWhenPasswordsDoNotMatch() {
        String token = "valid-token";
        String newPassword = "newPass123";
        String confirmPassword = "mismatchPass";

        assertThrows(PasswordChangeException.class, () ->
                userService.resetPassword(token, newPassword, confirmPassword));
    }

    @Test
    void deleteUser_shouldDeleteSuccessfully() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("benard@gmail.com");
        mockUser.setAccounts(List.of(new Account()));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        GenericResponse response = userService.deleteUser(userId);

        verify(tokenRepository).deleteAllByUser(mockUser);
        verify(accountRepository).deleteAll(mockUser.getAccounts());
        verify(userRepository).delete(mockUser);

        assertEquals(200, response.getStatus());
        assertEquals("User deleted successfully", response.getMessage());

    }

    @Test
    void deleteUser_shouldThrowExceptionIfUserNotFound() {
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser(userId));

        assertTrue(exception.getMessage().contains("User not found"));

    }

}