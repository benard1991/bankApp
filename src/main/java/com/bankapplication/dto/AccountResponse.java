package com.bankapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private Long accountId;
    private String accountNumber;
    private String accountType;
    private Double balance;
    private Long userId;
}
