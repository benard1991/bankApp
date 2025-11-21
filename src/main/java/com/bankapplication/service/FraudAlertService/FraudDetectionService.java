package com.bankapplication.service.FraudAlertService;


import com.bankapplication.model.Account;
import com.bankapplication.model.BlacklistedIp;
import com.bankapplication.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface FraudDetectionService {
    void checkHighValueTransactionDetection(Account account, Transaction tx, BigDecimal amount, String alertType,String alertDetails);
    void checkMultipleTransactionDetection(Account updatedAccount, Transaction tx, String alertType,String alertDetails);
    public void checkMultipleTransactionsFromIp(Account account, Transaction tx, String ip) ;
    public Page<BlacklistedIp> getAllBlacklistedIps( Pageable pageable);

}