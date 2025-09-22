package com.bankapplication.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotEmpty(message = "currentPassword  is required.")
    private String currentPassword;

    @NotEmpty(message = "newPassword is required.")
    private String newPassword;

    @NotEmpty(message = "confirmPassword is required.")
    private String confirmPassword;

}