package com.bankapplication.service.accountService;

import com.bankapplication.dto.AccountDto;
import com.bankapplication.exception.AccountNotFoundException;
import com.bankapplication.exception.UserNotFoundException;
import com.bankapplication.mapper.AccountMapper;
import com.bankapplication.model.Account;
import com.bankapplication.model.User;
import com.bankapplication.repository.AccountRepository;
import com.bankapplication.repository.TransactionRepository;
import com.bankapplication.repository.UserRepository;
import com.bankapplication.util.AccountNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              UserRepository userRepository,
                              TransactionRepository transactionRepository,
                              AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public Account createAccount(AccountDto accountDto) {
        log.info("Creating account for userId: {}", accountDto.getUserId());

        User user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + accountDto.getUserId()));

        Account account = new Account();
        account.setAccountType(accountDto.getAccountType());
        account.setBalance(accountDto.getBalance());
        account.setUser(user);

        String accountNumber = AccountNumberGenerator.generateAccountNumber();
        log.debug("Generated account number: {}", accountNumber);

        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = AccountNumberGenerator.generateAccountNumber();
            log.warn("Duplicate account number detected, regenerating...");
        }

        account.setAccountNumber(accountNumber);
        Account savedAccount = accountRepository.save(account);

        log.info("Account created successfully with accountNumber: {}", savedAccount.getAccountNumber());
        return savedAccount;
    }

    @Override
    @Cacheable(value = "accounts", key = "#userId")
    public Optional<Account> findById(Long userId) {
        log.info("Fetching account by user ID: {}", userId);
        return accountRepository.findById(userId);
    }

    @Override
//    @Cacheable(value = "accountsByUser", key = "#userId")
    public List<AccountDto> getAccountsByUserId(Long userId) {
        log.info("Fetching all accounts for userId: {}", userId);

        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            throw new UserNotFoundException("No accounts found for userId: " + userId);
        }

        List<AccountDto> accountDtos = accounts.stream()
                .map(account -> {
                    AccountDto dto = accountMapper.toAccountDto(account);
                    dto.setUserId(userId);
                    return dto;
                })
                .toList();

        log.info("Found {} accounts for userId: {}", accountDtos.size(), userId);
        return accountDtos;
    }



    @Override
    @Cacheable(value = "accountsByUser", key = "#accountNumber")
    public AccountDto getAccountByAccountNumber(String accountNumber) {
        log.info("Fetching account for account number: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found for account number: " + accountNumber));

        log.info("Account found for account number: {}", accountNumber);
        return accountMapper.toAccountDto(account);
    }
}
