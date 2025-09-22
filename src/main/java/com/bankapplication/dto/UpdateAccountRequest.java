package com.bankapplication.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateAccountRequest {

    @NotEmpty(message = "Firstname is required.")
    private String firstname;

    @NotEmpty(message = "Lastname is required.")
    private String lastname;

    @Min(value = 18, message = "Age must be at least 18.")
    private Integer age;

    private String nin; // Optional

    @NotEmpty(message = "BVN is required.")
    private String bvn;

    @NotEmpty(message = "State is required.")
    private String state;

    @NotEmpty(message = "Local Government is required.")
    private String localGovernment;

    @NotEmpty(message = "Phone number is required.")
//    @Pattern(regexp = "\\d{13}", message = "Phone number must be a 13-digit number.")
    private String phoneNumber;

    @NotEmpty(message = "Nationality is required.")
    private String nationality;

    private String religion;

    @NotEmpty(message = "Next of Kin First Name is required.")
    private String nextOfKinFirstName;

    @NotEmpty(message = "Next of Kin Last Name is required.")
    private String nextOfKinLastName;

    @NotEmpty(message = "Next of Kin Address is required.")
    private String nextOfKinAddress;

    @NotEmpty(message = "Next of Kin Phone number is required.")
//    @Pattern(regexp = "\\d{13}", message = "Next of Kin phone number must be a 13-digit number.")
    private String nextOfKinPhoneNumber;


}
