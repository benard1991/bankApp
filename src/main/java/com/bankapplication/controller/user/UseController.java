package com.bankapplication.controller.user;

import com.bankapplication.dto.GenericResponse;
import com.bankapplication.dto.ResetPasswordRequestDto;
import com.bankapplication.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UseController {

    private final UserService userService;


    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/forgot-password")
    public ResponseEntity<GenericResponse> forgotPassword(@RequestParam String email) {
        userService.createPasswordResetToken(email);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(null, "Password reset link sent", HttpStatus.OK.value()));
    }


    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/verify-reset-token")
    public ResponseEntity<GenericResponse> verifyResetToken(@RequestParam String token) {
        userService.verifyPasswordResetToken(token);
        return ResponseEntity.ok(
                new GenericResponse<>(null, "OTP Token is valid", HttpStatus.OK.value())
        );
    }


    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        userService.resetPassword(request.getOtp() ,request.getNewPassword(),request.getConfirmPassword());
        return ResponseEntity.ok(
                new GenericResponse<>(null, "Password has been successfully reset", HttpStatus.OK.value())
        );
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/{userId}/upload-image")
    public ResponseEntity<GenericResponse> uploadImage(
            @PathVariable Long userId,
            @RequestParam("image") MultipartFile imageFile) {

        String imageUrl = userService.uploadUserImage(userId, imageFile);

        return ResponseEntity.ok(
                new GenericResponse<>(imageUrl, "Image uploaded successfully", HttpStatus.OK.value())
        );

    }

}

