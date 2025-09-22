package com.bankapplication.repository;

import com.bankapplication.model.Account;
import com.bankapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository  extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findById(Long userId);


    List<Account> findByUser(User savedUser);
}
