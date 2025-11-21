package com.bankapplication.dto;

import com.bankapplication.model.FraudAlert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlertResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long transactionId;
    private String alertType;
    private String alertDetails;
    private LocalDateTime createdAt;
    private boolean resolved;

    public FraudAlertResponse(FraudAlert alert) {
        this.id = alert.getId();
        this.userId = alert.getUser() != null ? alert.getUser().getId() : null;
        this.username = alert.getUser() != null ? alert.getUser().getUsername() : null;
        this.transactionId = alert.getTransaction() != null ? alert.getTransaction().getId() : null;
        this.alertType = alert.getAlertType();
        this.alertDetails = alert.getAlertDetails();
        this.createdAt = alert.getCreatedAt();
        this.resolved = alert.isResolved();
    }
}

