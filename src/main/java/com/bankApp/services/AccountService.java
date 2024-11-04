package com.bankApp.services;

import com.bankApp.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AccountService {
        // Fetch all accounts with pagination
        Page<Account> findAll(Pageable pageable);

        // Fetch a single account by ID
        Optional<Account> findById(Long id);

        // Find an account by account number (if needed)
        Optional<Account> findByAccountNumber(String accountNumber);
    }


