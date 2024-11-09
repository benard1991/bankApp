package com.bankApp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationRequest {

    @Size(max = 20, message = "Last name must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name can only contain letters and spaces")
    private String lastName;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters and spaces")
    private String firstName;

    @Size(max = 20, message = "Other name must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name can only contain letters and spaces")
    private String otherName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be 18 or older")
    @Max(value = 100, message = "Age must be 100 or younger")
    private Integer age;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be at least 6 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{6,}$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one special character")
    private String password;

    @NotBlank(message = "NIN is required")
    @Pattern(regexp = "\\d{10}", message = "NIN must be exactly 10 digits")
    private String nin;

    @NotBlank(message = "BVN is required")
    @Pattern(regexp = "\\d{10}", message = "NIN must be exactly 10 digits")
    private String bvn;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    @NotBlank(message = "Account type is required")
    private String accountType;

    @NotBlank(message = "Next of kin last name is required")
    @Size(max = 20, message = "Next of kin last name must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Next of kin last name can only contain letters and spaces")
    private String nextOfKinLastName;

    @NotBlank(message = "Next of kin first name is required")
    @Size(max = 20, message = "Next of kin first name must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Next of kin first name can only contain letters and spaces")
    private String nextOfKinFirstName;


    @NotBlank(message = "Next of kin address is required")
    private String nextOfKinAddress;

    @NotBlank(message = "Next of kin occupation is required")
    private String nextOfKinOccupation;
}
