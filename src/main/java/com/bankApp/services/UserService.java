package com.bankApp.services;

import com.bankApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findByNin(Integer nin);

    Optional<User> findByBvn(Integer bvn);

    Optional<User> findByAccount_AccountNumber(String accountNumber);
}
