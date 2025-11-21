package com.bankapplication.service.FraudAlertService;


import com.bankapplication.dto.CreateFraudAlertRequestDto;
import com.bankapplication.dto.FraudAlertResponse;
import com.bankapplication.model.FraudAlert;

import java.util.List;

public interface FraudAlertService {

    // Updated to use DTOs
    FraudAlertResponse createAlert(CreateFraudAlertRequestDto request);

    List<FraudAlertResponse> getPendingAlerts();

    FraudAlertResponse resolveAlert(Long alertId);

    List<FraudAlertResponse> getAlertsByUser(Long userId);

    List<FraudAlertResponse> getAlertsByTransactionType(String transactionType);

    // Keep this for internal use (FraudDetectionService)
    FraudAlertResponse createAlertFromEntity(com.bankapplication.model.FraudAlert alert);

}
