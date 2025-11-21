package com.bankapplication.repository;

import com.bankapplication.model.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {

    // Find unresolved alerts
    List<FraudAlert> findByResolvedFalse();

    List<FraudAlert> findByUserId(Long userId);

    List<FraudAlert> findByTransaction_TransactionType(String transactionType);

    List<FraudAlert> findByAlertType(String alertType);
}
