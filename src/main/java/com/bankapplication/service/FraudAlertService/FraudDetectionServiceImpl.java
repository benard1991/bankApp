package com.bankapplication.service.FraudAlertService;

import com.bankapplication.model.Account;
import com.bankapplication.model.BlacklistedIp;
import com.bankapplication.model.FraudAlert;
import com.bankapplication.model.Transaction;
import com.bankapplication.repository.BlacklistedIpRepository;
import com.bankapplication.repository.TransactionRepository;
import com.bankapplication.service.mailService.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private final FraudAlertService fraudAlertService;
    private final TransactionRepository transactionRepository;
    private final BlacklistedIpRepository blacklistedIpRepository;
    private final EmailService emailService;

    @Value("${transaction.deposit-limit}")
    private BigDecimal depositLimit;

    @Value("${transaction.withdral_limt}")
    private BigDecimal  withdral_limt;

    @Value("${transaction.rapidTransactionThreshold:5}")
    private int rapidTransactionThreshold;

    @Override
    public   void checkHighValueTransactionDetection(Account account, Transaction tx, BigDecimal amount, String alertType,String alertDetails){
        if (amount.compareTo(withdral_limt) > 0) {

            FraudAlert alert = new FraudAlert();
            alert.setUser(account.getUser());
            alert.setTransaction(tx);
            alert.setAlertType(alertType);
            alert.setAlertDetails(alertDetails);

            emailService.sendDepositLimitAlert(
                    account.getUser().getUsername(),
                    account.getUser().getFirstname(),
                    amount,
                    withdral_limt
            );

            fraudAlertService.createAlertFromEntity(alert);
        }
    }

   public void checkMultipleTransactionDetection(Account account, Transaction tx, String alertType,String alertDetails){

       LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

       int depositCount = transactionRepository.countDepositsSince(
               account.getId(),
               tenMinutesAgo
       );
       if (depositCount > rapidTransactionThreshold) {

           FraudAlert alert = new FraudAlert();
           alert.setUser(account.getUser());
           alert.setTransaction(tx);
           alert.setAlertType(alertType);
           alert.setAlertDetails(alertDetails);

           fraudAlertService.createAlertFromEntity(alert);
       }
   }


    @Override
    public void checkMultipleTransactionsFromIp(Account account, Transaction tx, String ip) {

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        long count = transactionRepository.countByIpAndCreatedAtAfter(ip, fiveMinutesAgo);

        if (count >= 5) {
            FraudAlert alert = new FraudAlert();
            alert.setUser(account.getUser());
            alert.setTransaction(tx);
            alert.setAlertType("MULTIPLE_TRANSACTIONS_SAME_IP");
            alert.setAlertDetails("More than 5 transactions from IP: " + ip + " within 5 minutes.");
            fraudAlertService.createAlertFromEntity(alert);
        }
    }


    public Page<BlacklistedIp> getAllBlacklistedIps( Pageable pageable) {
        return blacklistedIpRepository.findAll(pageable);
    }


}