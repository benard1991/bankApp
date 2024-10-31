package com.bankApp.model;

import jakarta.persistence.*;
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

    private String lastName;
    private String firstName;
    private String otherName;
    private String email;
    private Integer age;  // Use lowercase "age" to match Java naming conventions
    private String password;
    private Integer nin;
    private Integer bvn;
    private String address;  // Use lowercase "address" to match Java naming conventions
    private String occupation;

    private String nextOfKinLastName;  // "netOfKin" is likely intended to be "nextOfKin"
    private String nextOfKinFirstName; // Correct "netOfKinFistName" to "nextOfKinFirstName"
    private String nextOfKinAddress;
    private String nextOfKinOccupation;

    private Boolean isActive = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FileUpload> files;
}
