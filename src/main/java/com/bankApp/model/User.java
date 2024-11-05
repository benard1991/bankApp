package com.bankApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Last name is required")
    @Size(max = 20, message = "Last name must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name can only contain letters and spaces")
    private String lastName;

    @NotBlank(message = "First name is required")
    @Size(max = 20, message = "First name must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "First name can only contain letters and spaces")
    private String firstName;

    @NotBlank(message = "Other name is required")
    @Size(max = 50, message = "Other name must be at most 50 characters")
    @Pattern(regexp = "^[A-Za-z\\s]*$", message = "Other name can only contain letters and spaces")
    private String otherName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be 18 or older")
    @Max(value = 100, message = "Age must be 100 or younger")
    private Integer age;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "NIN is required")
    @Pattern(regexp = "\\d{10}", message = "NIN must be exactly 10 digits")
    private Integer nin; // Change Integer to String for exact matching

    @NotBlank(message = "BVN is required")
    @Pattern(regexp = "\\d{10}", message = "BVN must be exactly 10 digits")
    private Integer bvn; // Change Integer to String for exact matching

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Address is required")
    private String accountNumber;

    @NotBlank(message = "Occupation is required")
    private String occupation;

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

    private Boolean isActive = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FileUpload> files;

    private String refreshToken;

    private Long refreshTokenExpirationTime;
}
