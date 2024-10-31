package com.bankApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Account number must be exactly 10 digits")
    private String accountNumber;

    @NotNull(message = "Balance is required")
    @Column(nullable = false)
    private Double balance = 0.0;

    @OneToOne(mappedBy = "account")
    private User user;

    private Boolean isActive = true;
}
