package com.bankapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {

    //  Basic user info
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private Integer age;
    private String nin;
    private String bvn;

    //  Address & location details
    private String state;
    private String localGovernment;
    private String nationality;

    //  Additional info
    private String imageUrl;
    private String religion;
    private String accountType;

    //  Next of Kin details
    private String nextOfKinFirstName;
    private String nextOfKinLastName;
    private String nextOfKinAddress;
    private String nextOfKinPhoneNumber;

    //  Timestamps
    private LocalDateTime createdAt;

    //  Related entities
    private List<AccountDto> accounts;
    private List<RoleDto> roles;

    //  Security flags
    private boolean active;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
}
