package com.bankapplication.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResetPasswordRequestDto {

    @NotEmpty(message = "Otp is required.")
    private String Otp;

    @NotEmpty(message = "newPassword is required.")
    private String newPassword;

    @NotEmpty(message = "confirmPassword is required.")
    private String confirmPassword;
}
