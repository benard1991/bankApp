package com.bankApp.Repository;

import com.bankApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Ensure this import is present
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Fetch all users with pagination
    Page<User> findAll(Pageable pageable);

    // Fetch a single user by ID
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByNin(String nin);

    Optional<User> findByBvn(String bvn);

    User save(User user);
    // Find a user by username
    Optional<User> findByAccount_AccountNumber(String accountNumber);
}
