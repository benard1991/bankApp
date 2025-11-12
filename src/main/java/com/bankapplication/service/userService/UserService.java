package com.bankapplication.service.userService;

import com.bankapplication.dto.*;
import com.bankapplication.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

public interface UserService {


    User createUser(UserDto userDto);

    public Map<String, Object> login(String username, String password);


    GenericResponse<UserProfileDto> getUserProfile(Long userId);

    User updateUser(Long userId, UpdateAccountRequest request);

    public GenericResponse deleteUser(Long userId);

    public GenericResponse changePassword(ChangePasswordRequest request, String username);

    public Page<User> getAllUsers(Pageable pageable);

    public User deactivateUser(Long userId);

    public User activateUser(Long userId);

    public void createPasswordResetToken(String email) ;

    void verifyPasswordResetToken(String token);

    void resetPassword(String token, String newPassword,String confirmPassword);

    String uploadUserImage(Long userId, MultipartFile file) ;
}


