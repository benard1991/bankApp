package com.bankapplication.service.transactionService;

import com.bankapplication.dto.TransactionDto;
import com.bankapplication.dto.TransactionResponseDto;
import com.bankapplication.exception.AccountNotFoundException;
import com.bankapplication.exception.InsufficientFundsException;
import com.bankapplication.exception.UserAlreadyExistsException;
import com.bankapplication.exception.UserNotFoundException;
import com.bankapplication.infrastructure.TransactionLockManager;
import com.bankapplication.model.Account;
import com.bankapplication.model.Transaction;
import com.bankapplication.model.User;
import com.bankapplication.repository.AccountRepository;
import com.bankapplication.repository.TransactionRepository;
import com.bankapplication.repository.UserRepository;
import com.bankapplication.service.AuditTrailService.AuditTrailService;
import com.bankapplication.service.FraudAlertService.FraudDetectionService;
import com.bankapplication.util.RequestUtils;
import com.bankapplication.util.TransactionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionUtils transactionUtils;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuditTrailService auditTrailService;
    private final RequestUtils requestUtils;
    private final TransactionLockManager lockManager;
    private final FraudDetectionService fraudDetectionService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Value("${transaction.withdral_limt}")
    private BigDecimal withdral_limt;

    @Value("${transaction.deposit-limit}")
    private BigDecimal depositLimit;



//      NOT transactional because it uses synchronized locking

    @Override
    public TransactionResponseDto handleTransaction(TransactionDto transactionDto, HttpServletRequest request) {
        logger.info("TransactionDto Request ===> {}", transactionDto);

        BigDecimal amount = BigDecimal.valueOf(transactionDto.getAmount());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserAlreadyExistsException.InvalidAmountException("Transaction amount must be greater than zero.");
        }

        String sourceAcc = transactionDto.getSourceAccountNumber();
        String destAcc = transactionDto.getDestinationAccountNumber();

        Object sourceLock = lockManager.getLock(sourceAcc);
        Object destLock = (destAcc != null && !destAcc.isEmpty())
                ? lockManager.getLock(destAcc)
                : new Object();

        // ORDER LOCKS to prevent deadlocks
        Object first = sourceAcc.compareTo(destAcc) < 0 ? sourceLock : destLock;
        Object second = sourceAcc.compareTo(destAcc) < 0 ? destLock : sourceLock;

        synchronized (first) {
            synchronized (second) {

                Account sourceAccount = accountRepository.findByAccountNumber(sourceAcc)
                        .orElseThrow(() -> new AccountNotFoundException("Source account not found."));

                if (sourceAccount.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException("Insufficient funds.");
                }

                String clientIp = requestUtils.getClientIp(request);

                auditTrailService.recordAudit(
                        sourceAccount.getUser().getId(),
                        sourceAccount.getUser().getUsername(),
                        "TRANSFER",
                        "Transaction",
                        sourceAccount.getId().toString(),
                        sourceAccount.getBalance().toPlainString(),
                        sourceAccount.getBalance().subtract(amount).toPlainString(),
                        "Initiated transfer of ₦" + amount + " from account " + sourceAcc,
                        clientIp,
                        "PENDING"
                );

                TransactionResponseDto response;

                String channel = transactionDto.getTransferChannel().toUpperCase();
                switch (channel) {
                    case "INTRA" -> response = intraBank(transactionDto, sourceAccount,clientIp);
                    case "INTER" -> response = interBank(transactionDto, sourceAccount, clientIp);
                    default -> throw new IllegalArgumentException("Invalid channel type: " + channel);
                }

                auditTrailService.recordAudit(
                        sourceAccount.getUser().getId(),
                        sourceAccount.getUser().getUsername(),
                        "TRANSFER",
                        "Transaction",
                        sourceAccount.getId().toString(),
                        sourceAccount.getBalance().toPlainString(),
                        sourceAccount.getBalance().subtract(amount).toPlainString(),
                        "Successfully transferred NGN" + amount + " via " + channel,
                        clientIp,
                        "SUCCESS"
                );

                return response;
            }
        }
    }



    @Transactional
    private TransactionResponseDto intraBank(TransactionDto dto, Account sourceAccount, String clientIp ) {

        Account destinationAccount = accountRepository.findByAccountNumber(dto.getDestinationAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found."));

        BigDecimal amount = BigDecimal.valueOf(dto.getAmount());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        transactionUtils.createTransaction(sourceAccount, dto.getAmount(), "DEBIT",
                dto.getSourceAccountNumber(), dto.getDestinationAccountNumber(),
                "INTRA", null, "SUCCESS",clientIp);

        transactionUtils.createTransaction(destinationAccount, dto.getAmount(), "CREDIT",
                dto.getSourceAccountNumber(), dto.getDestinationAccountNumber(),
                "INTRA", null, "SUCCESS", clientIp);

        return TransactionResponseDto.builder()
                .transactionType("DEBIT")
                .transferChannel("INTRA")
                .sourceAccount(sourceAccount.getAccountNumber())
                .destinationAccount(destinationAccount.getAccountNumber())
                .amount(dto.getAmount())
                .status("SUCCESS")
                .build();
    }


    @Transactional
    private TransactionResponseDto interBank(TransactionDto dto, Account sourceAccount, String clientIp) {

        BigDecimal amount = BigDecimal.valueOf(dto.getAmount());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        accountRepository.save(sourceAccount);


        transactionUtils.createTransaction(sourceAccount, dto.getAmount(), "DEBIT",
                dto.getSourceAccountNumber(), dto.getDestinationAccountNumber(),
                "INTER", dto.getDestinationBank(), "SUCCESS",clientIp);

        return TransactionResponseDto.builder()
                .transactionType("DEBIT")
                .transferChannel("INTER")
                .sourceAccount(sourceAccount.getAccountNumber())
                .destinationAccount(dto.getDestinationAccountNumber())
                .destinationBank(dto.getDestinationBank())
                .amount(dto.getAmount())
                .status("SUCCESS")
                .build();
    }



    @Override
    @Transactional
    public Account deposit(String accountNumber, double amountDouble, HttpServletRequest request) {

        BigDecimal amount = BigDecimal.valueOf(amountDouble);

        Object lock = lockManager.getLock(accountNumber);

        synchronized (lock) {

            Account account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found."));

            BigDecimal oldBalance = account.getBalance();
            BigDecimal newBalance = oldBalance.add(amount);

            account.setBalance(newBalance);
            Account updated = accountRepository.save(account);
            String clientIp = requestUtils.getClientIp(request);

            Transaction tx = transactionUtils.createTransaction(updated, amountDouble,
                    "CREDIT", accountNumber, null, "DEPOSIT", null, "SUCCESS",clientIp);


            auditTrailService.recordAudit(
                    account.getUser().getId(),
                    account.getUser().getUsername(),
                    "DEPOSIT",
                    "Account",
                    account.getId().toString(),
                    oldBalance.toPlainString(),
                    newBalance.toPlainString(),
                    "User deposited NGN" + amount.toPlainString() + " into account " + accountNumber,
                    clientIp,
                    "SUCCESS"
            );

            fraudDetectionService.checkHighValueTransactionDetection(
                    updated, tx, amount, "HIGH_VALUE_DEPOSIT",
                    "Deposit of NGN" + amount.toPlainString() + " exceeds NGN" + depositLimit + " limit."
            );

            fraudDetectionService.checkMultipleTransactionDetection(account, tx,
                    "RAPID_DEPOSITS", "More than 5 deposits within the last 10 minutes.");

            fraudDetectionService.checkMultipleTransactionsFromIp(account, tx, clientIp);

            return updated;
        }
    }



    @Override
    @Transactional
    public Account withdraw(String accountNumber, double amountDouble, HttpServletRequest request) {

        BigDecimal amount = BigDecimal.valueOf(amountDouble);

        Object lock = lockManager.getLock(accountNumber);

        synchronized (lock) {

            Account account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found."));

            if (account.getBalance().compareTo(amount) < 0) {
                throw new UserAlreadyExistsException.InvalidAmountException("Insufficient balance.");
            }

            BigDecimal oldBalance = account.getBalance();
            BigDecimal newBalance = oldBalance.subtract(amount);

            account.setBalance(newBalance);
            Account updated = accountRepository.save(account);

            String clientIp = requestUtils.getClientIp(request);

            Transaction tx = transactionUtils.createTransaction(updated, amountDouble,
                    "DEBIT", accountNumber, null, "WITHDRAWAL", null, "SUCCESS", clientIp
            );


            fraudDetectionService.checkHighValueTransactionDetection(
                    updated, tx, amount,
                    "HIGH_VALUE_WITHDRAWAL",
                    "Withdrawal of NGN" + amount.toPlainString() + " exceeds NGN" + withdral_limt + " limit."
            );

            fraudDetectionService.checkMultipleTransactionDetection(account, tx,
                    "RAPID_WITHDRAWAL", "More than 5 withdrawals within the last 10 minutes.");

            fraudDetectionService.checkMultipleTransactionsFromIp(updated, tx, clientIp);

            auditTrailService.recordAudit(
                    account.getUser().getId(),
                    account.getUser().getUsername(),
                    "WITHDRAWAL",
                    "Account",
                    account.getId().toString(),
                    oldBalance.toPlainString(),
                    newBalance.toPlainString(),
                    "User withdrew NGN" + amount.toPlainString() + " from account " + accountNumber,
                    clientIp,
                    "SUCCESS"
            );

            return updated;
        }
    }

    @Override
    public Page<Transaction> getTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    public Page<Transaction> getUserTransaction(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return transactionRepository.findByUserId(userId, pageable);
    }
}
