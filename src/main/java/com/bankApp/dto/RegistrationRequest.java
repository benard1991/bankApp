package com.bankApp.dto;

import lombok.Data;

@Data
public class RegistrationRequest {

    private String lastName;
    private String firstName;
    private String otherName;
    private String email;
    private Integer age;
    private String password;
    private Integer nin;
    private Integer bvn;
    private String address;
    private String occupation;


    private String nextOfKinLastName;
    private String nextOfKinFirstName;
    private String nextOfKinAddress;
    private String nextOfKinOccupation;





}


