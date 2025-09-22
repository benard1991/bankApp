package com.bankapplication.controller.auth;




import com.bankapplication.dto.GenericResponse;
import com.bankapplication.dto.LoginRequet;
import com.bankapplication.dto.UserDto;
import com.bankapplication.model.User;
import com.bankapplication.service.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                .body(new GenericResponse<>( registeredUser,"User created successfully",HttpStatus.CREATED.value()));
    }


    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@RequestBody @Valid LoginRequet requet) {
        Map<String, Object> tokens = userService.login(requet.getUsername(), requet.getPassword());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>( tokens,"Login successful",HttpStatus.OK.value()));
    }


}