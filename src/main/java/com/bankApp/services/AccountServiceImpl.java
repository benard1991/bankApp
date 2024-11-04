package com.bankApp.services;

import com.bankApp.Repository.AccountRepository;
import com.bankApp.exceptionHandler.AccountNotFoundException;
import com.bankApp.model.Account;
import com.bankApp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }



    @Override
    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id)
                .or(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id);
                });
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .or(() -> {
                    throw new AccountNotFoundException( "User with account Number : " + accountNumber+ "not found");
                });
    }
}
