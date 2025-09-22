package com.bankapplication.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private Long id;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    private String status;

    private String transactionType;

    private String referenceNumber;

    @NotNull(message = "transferChannel type cannot be null")
    @Pattern(regexp = "^(INTRA|INTER)$", message = "transferChannel type must be either INTRA or INTER")
    private String transferChannel;

    private  String  destinationBank;

    private LocalDateTime transactionDate;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Source account cannot be null")
    private String sourceAccountNumber;

    @NotNull(message = "Destination account cannot be null")
    private String destinationAccountNumber;
}
