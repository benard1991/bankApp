package com.bankApp.Repository;

import com.bankApp.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Fetch all accounts with pagination
    Page<Account> findAll(Pageable pageable);


    // Find an account by account number (if needed)
    Optional<Account> findByAccountNumber(String accountNumber);
}
