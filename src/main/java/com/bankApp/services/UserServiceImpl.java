package com.bankApp.services;

import com.bankApp.exceptionHandler.AccountNotFoundException;
import com.bankApp.exceptionHandler.UserNotFoundExeption;
import com.bankApp.model.User;
import com.bankApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id)
                .or(() -> {
                    throw new UserNotFoundExeption("User not found with ID: " + id);
                });
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .or(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email);
                });
    }

    @Override
    public Optional<User> findByAccount_AccountNumber(String accountNumber) {
        return userRepository.findByAccount_AccountNumber(accountNumber)
                .or(() -> {
                    throw new AccountNotFoundException( "User not found with account number: " + accountNumber);
                });
    }
}