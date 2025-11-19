package com.bankapplication.controller.auth;




import com.bankapplication.dto.GenericResponse;
import com.bankapplication.dto.LoginRequet;
import com.bankapplication.dto.OtpRequestDto;
import com.bankapplication.dto.UserDto;
import com.bankapplication.model.User;
import com.bankapplication.service.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> register(@RequestBody @Valid UserDto userDto) {
        User registeredUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(registeredUser, "User created successfully", HttpStatus.CREATED.value()));
    }


    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@RequestBody @Valid LoginRequet request) {
        userService.initiateLogin(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new GenericResponse<>(null,
                "Password verified. OTP sent to your email. Please verify to complete login.",
                HttpStatus.OK.value()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<GenericResponse> verifyOtp(@RequestBody  @Valid OtpRequestDto request) {
        Map<String, Object> response = userService.completeLogin(request.getUsername(), request.getOtp());
        return ResponseEntity.ok(new GenericResponse<>(response, "Login successful", HttpStatus.OK.value()));
    }

}