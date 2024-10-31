package com.bankApp.model;

import jakarta.persistence.*;

public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private Double balance = 0.0;


    @OneToOne(mappedBy = "account")
    private User user;

    private Boolean isActive = true;
}
