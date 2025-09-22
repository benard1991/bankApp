package com.bankapplication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequet {

    @NotEmpty(message = "Username is required.")
    @Email(message = "Username must be a valid email.")
    private String username;

    @NotEmpty(message = "Password is required.")
    private String password;
}
