package com.bankapplication.dto;

import com.bankapplication.model.enums.AccountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDto {

    @NotNull(message = "Account type cannot be null")
    private AccountType accountType;

    private String accountNumber; // You will generate this on the backend

    @Min(value = 0, message = "Balance cannot be less than 0")
    private Double balance = 0.00;

    @NotNull(message = "User ID cannot be null")
    private Long userId;


}