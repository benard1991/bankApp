package com.bankapplication.service.accountService;

import com.bankapplication.dto.AccountDto;
import com.bankapplication.exception.AccountNotFoundException;
import com.bankapplication.exception.UserNotFoundException;
import com.bankapplication.model.Account;
import com.bankapplication.model.Transaction;
import com.bankapplication.model.User;
import com.bankapplication.repository.AccountRepository;
import com.bankapplication.repository.TransactionRepository;
import com.bankapplication.repository.UserRepository;
import com.bankapplication.util.AccountNumberGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl  implements AccountService {

    private final AccountRepository accountRepository;
    private  final UserRepository userRepository;
    private  final TransactionRepository transactionRepository;

    @Autowired
    public  AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository, TransactionRepository transactionRepository) {

        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }


    @Override
    public Account createAccount(AccountDto accountDto) {
        User  user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + accountDto.getUserId()));

        Account account = new Account();
        account.setAccountType(accountDto.getAccountType());
        account.setBalance(accountDto.getBalance());
        account.setUser(user);

        String accountNumber = AccountNumberGenerator.generateAccountNumber();
        System.out.println("account =======> "+accountNumber);

        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = AccountNumberGenerator.generateAccountNumber();
        }

        account.setAccountNumber(accountNumber);

        return accountRepository.save(account);
    }


    @Override
    public Optional<Account> findById(Long userId) {

        return accountRepository.findById(userId);
    }

    public List<Account> getAccountsByUserId(Long userId) {

        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            throw new UserNotFoundException("No accounts found for userId: " + userId);
        }

        return accounts;
    }



    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            throw  new AccountNotFoundException("Account not found for account number: " + accountNumber);
        }

        return account;
    }







}
