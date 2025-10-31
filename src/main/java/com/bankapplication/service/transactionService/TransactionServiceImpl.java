package com.bankapplication.service.transactionService;

import com.bankapplication.dto.TransactionDto;
import com.bankapplication.dto.TransactionResponseDto;
import com.bankapplication.exception.AccountNotFoundException;
import com.bankapplication.exception.InsufficientFundsException;
import com.bankapplication.exception.UserAlreadyExistsException;
import com.bankapplication.exception.UserNotFoundException;
import com.bankapplication.model.Account;
import com.bankapplication.model.Transaction;
import com.bankapplication.model.User;
import com.bankapplication.repository.AccountRepository;
import com.bankapplication.repository.TransactionRepository;
import com.bankapplication.repository.UserRepository;
import com.bankapplication.service.AuditTrailService.AuditTrailService;
import com.bankapplication.service.util.TransactionUtils;
import com.bankapplication.util.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionUtils transactionUtils;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private  final AuditTrailService auditTrailService;
    private  final RequestUtils requestUtils;



    @Override
    public TransactionResponseDto handleTransaction(TransactionDto transactionDto, HttpServletRequest request) {
        logger.info("TransactionDto Request ===> {}", transactionDto);

        if (transactionDto.getAmount() <= 0) {
            throw new UserAlreadyExistsException.InvalidAmountException("Transaction amount must be greater than zero.");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(transactionDto.getSourceAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found."));
        logger.info("Source account found ===> {}", sourceAccount.getAccountNumber());

        if (sourceAccount.getBalance() < transactionDto.getAmount()) {
            logger.warn("Insufficient funds for account {}", sourceAccount.getAccountNumber());
            throw new InsufficientFundsException("Insufficient funds.");
        }

        // Get client IP
        String clientIp = requestUtils.getClientIp(request);

        // Record Audit Trail (example before performing transaction)
        auditTrailService.recordAudit(
                sourceAccount.getUser().getId(),
                sourceAccount.getUser().getUsername(),
                "TRANSFER",
                "Transaction",
                String.valueOf(sourceAccount.getId()),
                String.valueOf(sourceAccount.getBalance()),
                String.valueOf(sourceAccount.getBalance() - transactionDto.getAmount()),
                "Initiated transfer of ₦" + transactionDto.getAmount() + " from account " + sourceAccount.getAccountNumber(),
                clientIp,
                "PENDING"
        );

        // Determine transfer channel
        String channel = transactionDto.getTransferChannel().toUpperCase();
        logger.info("Switching transaction channel ===> {}", channel);

        TransactionResponseDto response;

        switch (channel) {
            case "INTRA" -> response = handleIntraBankTransaction(transactionDto, sourceAccount);
            case "INTER" -> response = handleInterBankTransaction(transactionDto, sourceAccount);
            default -> throw new IllegalArgumentException("Unsupported transfer channel: " + channel);
        }

        // Update audit trail after success
        auditTrailService.recordAudit(
                sourceAccount.getUser().getId(),
                sourceAccount.getUser().getUsername(),
                "TRANSFER",
                "Transaction",
                String.valueOf(sourceAccount.getId()),
                String.valueOf(sourceAccount.getBalance()),
                String.valueOf(sourceAccount.getBalance() - transactionDto.getAmount()),
                "Successfully transferred ₦" + transactionDto.getAmount() + " via " + channel + " channel.",
                clientIp,
                "SUCCESS"
        );

        return response;
    }

    private TransactionResponseDto handleIntraBankTransaction(TransactionDto dto, Account sourceAccount) {
        if (dto.getDestinationAccountNumber() == null) {
            throw new IllegalArgumentException("Destination account number is required for INTRA-bank transfers.");
        }

        Account destinationAccount = accountRepository.findByAccountNumber(dto.getDestinationAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found."));

        logger.info("destinationAccount === " + destinationAccount.getBalance());

        sourceAccount.setBalance(sourceAccount.getBalance() - dto.getAmount());
        destinationAccount.setBalance(destinationAccount.getBalance() + dto.getAmount());

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        transactionUtils.createTransaction(sourceAccount, dto.getAmount(), "DEBIT", dto.getSourceAccountNumber(), dto.getDestinationAccountNumber(), "INTRA", null, "SUCCESS");
        transactionUtils.createTransaction(destinationAccount, dto.getAmount(), "CREDIT", dto.getSourceAccountNumber(), dto.getDestinationAccountNumber(), "INTRA", null, "SUCCESS");



        return TransactionResponseDto.builder()
                .transactionType("DEBIT")
                .transferChannel("INTRA")
                .sourceAccount(sourceAccount.getAccountNumber())
                .destinationAccount(destinationAccount.getAccountNumber())
                .destinationBank(null)
                .amount(dto.getAmount())
                .status("SUCCESS")
                .build();

    }

    private TransactionResponseDto handleInterBankTransaction(TransactionDto dto, Account sourceAccount) {
        if (dto.getDestinationBank() == null || dto.getDestinationBank().isBlank()) {
            throw new IllegalArgumentException("Destination bank is required for INTER-bank transfers.");
        }

        sourceAccount.setBalance(sourceAccount.getBalance() - dto.getAmount());
        accountRepository.save(sourceAccount);

        transactionUtils.createTransaction(sourceAccount, dto.getAmount(), "DEBIT", dto.getSourceAccountNumber(), dto.getDestinationAccountNumber(), "INTER", dto.getDestinationBank(), "SUCCESS");

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


    @Transactional
    @Override
    public Account deposit(String accountNumber, double amount, HttpServletRequest request) {
        if (amount <= 0) {
            throw new UserAlreadyExistsException.InvalidAmountException("Deposit amount must be greater than zero.");
        }
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for account number: " + accountNumber));

        account.setBalance(account.getBalance() + amount);

        Account updatedAccount = accountRepository.save(account);


        transactionUtils.createTransaction(updatedAccount, amount, "CREDIT", accountNumber, null, "DEPOSIT", null, "SUCCESS");

        // Add Audit Trail here
        String clientIp = requestUtils.getClientIp(request);
        auditTrailService.recordAudit(
                account.getUser().getId(),
                account.getUser().getUsername(),
                "DEPOSIT",
                "Account",
                String.valueOf(account.getId()),
                String.valueOf(account.getBalance()),
                String.valueOf(updatedAccount.getBalance()),
                "User deposited ₦" + amount + " from account " + accountNumber,
                clientIp,
                "SUCCESS"
        );

        return updatedAccount;
    }

    @Override
    public Account withdraw(String accountNumber, double amount,  HttpServletRequest request) {
        if (amount <= 0) {
            throw new UserAlreadyExistsException.InvalidAmountException("withdraw amount must be greater than zero.");
        }
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for account number: " + accountNumber));

        account.setBalance(account.getBalance() - amount);

        Account updatedAccount = accountRepository.save(account);

        transactionUtils.createTransaction(updatedAccount, amount, "CREDIT", accountNumber, null, "WITHDRAWAL", null, "SUCCESS");

        // Add Audit Trail here
        String clientIp = requestUtils.getClientIp(request);
              auditTrailService.recordAudit(
                account.getUser().getId(),
                account.getUser().getUsername(),
                "WITHDRAWAL",
                "Account",
                String.valueOf(account.getId()),
                String.valueOf(account.getBalance()),
                String.valueOf(updatedAccount.getBalance()),
                "User withdrew ₦" + amount + " from account " + accountNumber,
                      clientIp,
                "SUCCESS"
        );

        return updatedAccount;
    }

    @Override
    public Page<Transaction> getTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    public Page<Transaction> getUserTransaction(Long userId, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return transactionRepository.findByUserId(userId, pageable);
    }


}
