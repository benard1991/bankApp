package com.bankapplication.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class OtpRequestDto {

    @NotEmpty(message = "Username is required.")
    private String username;

    @NotEmpty(message = "OTP is required.")
    private String otp;
}
