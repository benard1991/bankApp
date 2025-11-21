package com.bankapplication.service.FraudAlertService;

import com.bankapplication.dto.CreateFraudAlertRequestDto;
import com.bankapplication.dto.FraudAlertResponse;
import com.bankapplication.model.FraudAlert;
import com.bankapplication.model.Transaction;
import com.bankapplication.model.User;
import com.bankapplication.repository.FraudAlertRepository;
import com.bankapplication.repository.TransactionRepository;
import com.bankapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FraudAlertServiceImpl implements FraudAlertService {

    private final FraudAlertRepository repository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public FraudAlertResponse createAlert(CreateFraudAlertRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        FraudAlert alert = new FraudAlert();
        alert.setUser(user);
        alert.setAlertType(request.getAlertType());
        alert.setAlertDetails(request.getAlertDetails());
        alert.setCreatedAt(LocalDateTime.now());
        alert.setResolved(false);

        if (request.getTransactionId() != null) {
            Transaction transaction = transactionRepository.findById(request.getTransactionId())
                    .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + request.getTransactionId()));
            alert.setTransaction(transaction);
        }

        FraudAlert savedAlert = repository.save(alert);
        return new FraudAlertResponse(savedAlert);
    }

    @Override
    @Transactional
    public FraudAlertResponse createAlertFromEntity(FraudAlert alert) {
        FraudAlert savedAlert = repository.save(alert);
        return new FraudAlertResponse(savedAlert);
    }

    @Override
    public List<FraudAlertResponse> getPendingAlerts() {
        return repository.findByResolvedFalse()
                .stream()
                .map(FraudAlertResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FraudAlertResponse resolveAlert(Long alertId) {
        FraudAlert alert = repository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found with ID: " + alertId));
        alert.setResolved(true);
        FraudAlert resolved = repository.save(alert);
        return new FraudAlertResponse(resolved);
    }

    @Override
    public List<FraudAlertResponse> getAlertsByUser(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(FraudAlertResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<FraudAlertResponse> getAlertsByTransactionType(String transactionType) {
        return repository.findByTransaction_TransactionType(transactionType)
                .stream()
                .map(FraudAlertResponse::new)
                .collect(Collectors.toList());
    }
}
