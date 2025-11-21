package com.bankapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFraudAlertRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long transactionId;  // Optional

    @NotBlank(message = "Alert type is required")
    private String alertType;

    @NotBlank(message = "Alert details are required")
    private String alertDetails;
}
