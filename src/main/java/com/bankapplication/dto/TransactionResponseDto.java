package com.bankapplication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDto {
    private String transactionType;
    private String transferChannel;
    private String sourceAccount;
    private String destinationAccount;
    private String destinationBank;
    private double amount;
    private String status;


}
