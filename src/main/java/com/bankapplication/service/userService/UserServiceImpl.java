package com.bankapplication.service.userService;

import com.bankapplication.dto.*;
import com.bankapplication.exception.*;
import com.bankapplication.mapper.UserMapper;
import com.bankapplication.model.Account;
import com.bankapplication.model.PasswordResetToken;
import com.bankapplication.model.Role;
import com.bankapplication.model.User;
import com.bankapplication.model.enums.AccountType;
import com.bankapplication.model.enums.UserRole;
import com.bankapplication.repository.AccountRepository;
import com.bankapplication.repository.PasswordResetTokenRepository;
import com.bankapplication.repository.TokenRepository;
import com.bankapplication.repository.UserRepository;
import com.bankapplication.service.JwtService;
import com.bankapplication.service.mailService.EmailService;
import com.bankapplication.service.roleService.RoleService;
import com.bankapplication.service.tokenService.TokenService;
import com.bankapplication.util.AccountNumberGenerator;
import com.bankapplication.util.ResetPasswordOtp;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final ResetPasswordOtp resetPasswordOtp;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private  final TokenService tokenService;
    private final Cloudinary cloudinary;
    private final UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    public Map<String, Object> login(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            User user = (User) authentication.getPrincipal();
            logger.info("Authenticated user " + user);

            Map<String, Object> tokens = jwtService.generateAccessAndRefreshTokens(user);
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokens.get("accessToken"));
            response.put("refreshToken", tokens.get("refreshToken"));
            response.put("accessTokenExpiration", tokens.get("accessTokenExpiration"));
            response.put("refreshTokenExpiration", tokens.get("refreshTokenExpiration"));

            // Add user info to the response
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("firstname", user.getFirstname());
            userInfo.put("lastname", user.getLastname());
            userInfo.put("email", user.getUsername()); // Using username as email
            userInfo.put("phoneNumber", user.getPhoneNumber());
            userInfo.put("role", user.getRoles().stream().map(role -> role.getName()).collect(Collectors.joining(", "))); // Role as a single string
            userInfo.put("age", user.getAge());
            userInfo.put("nationality", user.getNationality());
            userInfo.put("religion", user.getReligion());
            userInfo.put("accountType", user.getAccountType());
            userInfo.put("localGovernment", user.getLocalGovernment());
            userInfo.put("bvn", user.getBvn());
            userInfo.put("nin", user.getNin());
            userInfo.put("state", user.getState());
            userInfo.put("nextOfKinFirstName", user.getNextOfKinFirstName());
            userInfo.put("nextOfKinLastName", user.getNextOfKinLastName());
            userInfo.put("nextOfKinPhoneNumber", user.getNextOfKinPhoneNumber());
            userInfo.put("nextOfKinAddress", user.getNextOfKinAddress());

            response.put("user", userInfo);

            emailService.sendLoginNotification(user.getUsername(), user.getFirstname());

            return response;

        } catch (Exception ex) {
            throw new InvalideLoginCredentials("Invalid login credentails");
        }
    }


    @Override
    public User createUser(UserDto userDto) {
        if (userDto.getRole() == null || userDto.getRole().isEmpty()) {
            throw new InvalidRoleException("Role policy is required.");
        }

        if (userDto.getRole().stream().anyMatch(role -> role.equalsIgnoreCase("CUSTOMER"))) {
            if (userDto.getNextOfKinFirstName() == null ||
                    userDto.getNextOfKinLastName() == null ||
                    userDto.getNextOfKinPhoneNumber() == null) {
                throw new InvalidNextOfKinDetailsException("Next of Kin details are required for the CUSTOMER role.");
            }
        }

        if (userDto.getUsername() != null && !userDto.getUsername().isEmpty()) {
            Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
            if (existingUser.isPresent()) {
                throw new UserAlreadyExistsException("User with this username already exists");
            }
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setFirstname(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setAge(userDto.getAge());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setNationality(userDto.getNationality());
        user.setReligion(userDto.getReligion());
        user.setAccountType(userDto.getAccountType());
        user.setLocalGovernment(userDto.getLocalGovernment());
        user.setBvn(userDto.getBvn());
        user.setNin(userDto.getNin());
        user.setState(userDto.getState());
        user.setNextOfKinFirstName(userDto.getNextOfKinFirstName());
        user.setNextOfKinLastName(userDto.getNextOfKinLastName());
        user.setNextOfKinAddress(userDto.getNextOfKinAddress());
        user.setNextOfKinPhoneNumber(userDto.getNextOfKinPhoneNumber());

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);

        AccountType requestedType;
        try {
            requestedType = AccountType.valueOf(userDto.getAccountType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidAccountTypeException("Invalid account type provided: " + userDto.getAccountType());
        }

        List<Account> existingAccounts = accountRepository.findByUser(savedUser);
        if (existingAccounts.size() >= 2) {
            throw new AccountLimitExceededException("User cannot have more than 2 accounts.");
        }

        boolean hasSameType = existingAccounts.stream()
                .anyMatch(account -> account.getAccountType().equals(requestedType));
        if (hasSameType) {
            throw new DuplicateAccountTypeException("User already has a " + requestedType + " account.");
        }

        Account account = new Account();
        account.setAccountNumber(AccountNumberGenerator.generateAccountNumber());
        account.setAccountType(requestedType);
        account.setBalance(0.00);
        account.setUser(savedUser);
        accountRepository.save(account);

        if (savedUser.getAccounts() == null) {
            savedUser.setAccounts(new ArrayList<>());
        }
        savedUser.getAccounts().add(account);

        Set<Role> userRoles = new HashSet<>();
        for (String roleStr : userDto.getRole()) {
            try {
                UserRole userRoleEnum = UserRole.valueOf(roleStr.trim().toUpperCase());
                Role role = roleService.getOrCreateRole(userRoleEnum);
                if (role.getUsers() == null) {
                    role.setUsers(new HashSet<>());
                }
                role.getUsers().add(user);
                userRoles.add(role);
            } catch (IllegalArgumentException e) {
                throw new InvalidRoleException("Invalid role: " + roleStr);
            }
        }
        user.setRoles(userRoles);

        emailService.sendRegistrationEmail(user.getUsername(), user.getFirstname());

        return userRepository.save(savedUser);
    }



    @Override
//    @Cacheable(value = "userProfiles", key = "#userId")
    public GenericResponse<UserProfileDto> getUserProfile(Long userId) {
        logger.info("Fetching user profile for ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

        UserProfileDto profileDto = userMapper.toUserProfileDto(user);

        logger.info(" User profile successfully fetched for ID: {}", userId);

        return new GenericResponse<>(
                profileDto,
                "User profile retrieved successfully",
                HttpStatus.OK.value()
        );
    }



    @Override
    public User updateUser(Long userId, UpdateAccountRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if ("CUSTOMER".equalsIgnoreCase(existingUser.getRoles().iterator().next().getName())) {
            if (request.getNextOfKinFirstName() == null || request.getNextOfKinLastName() == null || request.getNextOfKinPhoneNumber() == null) {
                logger.info("Next of kin is required for Customers");
                throw new InvalidNextOfKinDetailsException("Next of Kin details are required for the CUSTOMER role.");

            }
        }

        existingUser.setFirstname(request.getFirstname());
        existingUser.setLastname(request.getLastname());
        existingUser.setAge(request.getAge());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        existingUser.setNationality(request.getNationality());
        existingUser.setReligion(request.getReligion());
        existingUser.setLocalGovernment(request.getLocalGovernment());
        existingUser.setBvn(request.getBvn());
        existingUser.setNin(request.getNin());
        existingUser.setState(request.getState());
        existingUser.setNextOfKinFirstName(request.getNextOfKinFirstName());
        existingUser.setNextOfKinAddress(request.getNextOfKinAddress());
        existingUser.setNextOfKinLastName(request.getNextOfKinLastName());
        existingUser.setNextOfKinPhoneNumber(request.getNextOfKinPhoneNumber());

        return userRepository.save(existingUser);
    }


    @Override
    public GenericResponse deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        tokenRepository.deleteAllByUser(user);

        if (user.getAccounts() != null && !user.getAccounts().isEmpty()) {
            accountRepository.deleteAll(user.getAccounts());
        }
        userRepository.delete(user);
        logger.info("User deleted successfully");
        return new GenericResponse(null, "User deleted successfully", HttpStatus.OK.value());
    }


    @Override
    public GenericResponse changePassword(ChangePasswordRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        validatePasswordChange(request, user);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        emailService.sendPasswordChangeConfirmation(user.getUsername(), user.getFirstname());

        return new GenericResponse(null, "Password changed successfully", HttpStatus.OK.value());
    }


    private void validatePasswordChange(ChangePasswordRequest request, User user) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            logger.info("Current password is incorrect");
            throw new PasswordChangeException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordChangeException("New password and confirmation do not match");
        }
    }

    @Override
    @Cacheable(value = "users", key = "'page_' + #pageable.pageNumber + '_size_' + #pageable.pageSize + '_sort_' + #pageable.sort.toString()")
    public Page<User> getAllUsers(Pageable pageable) {
        try {
            logger.info("Fetching user from the databases");
            return userRepository.findAll(pageable);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Database error occurred while fetching users", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error while fetching users", ex);
        }
    }

    @Override
    public User deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for ID: " + userId));

        if (!user.isActive()) {
            throw new UserAlreadyActiveException("User account is already deactivated for ID: " + userId);
        }

        user.setActive(false);

        emailService.sendAccountDeactivatedEmail(user.getUsername(), user.getFirstname());

        return userRepository.save(user);
    }


    @Override
    public User activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for ID: " + userId));

        if (user.isActive()) {
            throw new UserAlreadyActiveException("User account is already Active for ID: " + userId);
        }

        user.setActive(false);

        emailService.sendAccountActivatedEmail(user.getUsername(), user.getFirstname());

        return userRepository.save(user);
    }


    @Override
    public void createPasswordResetToken(String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        passwordResetTokenRepository.deleteByUser(user);

        String otp = resetPasswordOtp.generateResetToken();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        PasswordResetToken token = PasswordResetToken.builder()
                .otp(otp)
                .user(user)
                .expiryTime(expiry)
                .build();

        logger.info("create password restToken Builder===== " + token);

        passwordResetTokenRepository.save(token);

        emailService.sendResetPasswordLink(email, otp, user.getFirstname());

    }


    @Override
    public void verifyPasswordResetToken(String token) {
        tokenService.validateResetToken(token);

    }


    @Override
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordChangeException("Passwords do not match.");
        }

        PasswordResetToken resetToken = tokenService.validateResetToken(token);
        logger.info("Password restToken ===== " + token);

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        emailService.sendPasswordChangeConfirmation(user.getUsername(), user.getFirstname());
    }

    @Override
    public String uploadUserImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB

            logger.info("Uploaded file is too large. Max size allowed is 5MB");

            throw new ImageUploadException("Uploaded file is too large. Max size allowed is 5MB.");
        }

        try {
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "transformation", new Transformation()
                            .width(300)
                            .height(300)
                            .crop("fill")
                            .gravity("auto")
                            .quality("auto")
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String imageUrl = (String) uploadResult.get("secure_url");

            user.setImageUrl(imageUrl);
            userRepository.save(user);

            logger.info("Image URL=== " + imageUrl);

            return imageUrl;

        } catch (IOException e) {
            logger.info("Image failed to uupload");
            throw new ImageUploadException("Failed to upload image to Cloudinary", e);
        }
    }





}
