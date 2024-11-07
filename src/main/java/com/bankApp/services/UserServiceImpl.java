package com.bankApp.services;

import com.bankApp.exceptionHandler.AccountNotFoundException;
import com.bankApp.exceptionHandler.UserNotFoundExeption;
import com.bankApp.model.User;
import com.bankApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        return userRepository.findById(id);
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByNin(Integer nin) {
        return userRepository.findByNin(nin);
    }

    @Override
    public Optional<User> findByBvn(Integer bvn) {
        return userRepository.findByBvn(bvn);
    }

    @Override
    public Optional<User> findByAccount_AccountNumber(String accountNumber) {
        return userRepository.findByAccount_AccountNumber(accountNumber);

    }


    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        // You should validate the password here
        return user;
    }



//    public User loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//        return user;
//    }
}
