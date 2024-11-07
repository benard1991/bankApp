package com.bankApp.controller;

import com.bankApp.dto.RegistrationRequest;
import com.bankApp.exceptionHandler.BvnExistException;
import com.bankApp.exceptionHandler.EmailNotFoundException;
import com.bankApp.exceptionHandler.NinExistException;
import com.bankApp.model.Account;
import com.bankApp.model.Role;
import com.bankApp.model.User;
import com.bankApp.security.JwtUtil;
import com.bankApp.security.PasswordEncoderUtil;
import com.bankApp.services.UserService;
import com.bankApp.util.CustomResponse;
import com.bankApp.util.AccountNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<CustomResponse<String>> register(@RequestBody RegistrationRequest registrationRequest) {

        // Check if email already exists
        if (userService.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new EmailNotFoundException("Email already exists!");
        }

        // Check if BVN already exists
        if (userService.findByBvn(registrationRequest.getBvn()).isPresent()) {
            throw new BvnExistException("BVN already exists!");
        }

        // Check if NIN already exists
        if (userService.findByNin(registrationRequest.getNin()).isPresent()) {
            throw new NinExistException("NIN already exists!");
        }

        // Generate a new account and account number
        Account account = new Account();
        String accountNumber = AccountNumberGenerator.generateAccountNumber();
        account.setAccountNumber(accountNumber);

        // Create the user and set the necessary fields
        User user = new User();
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setAddress(registrationRequest.getAddress());
        user.setAge(registrationRequest.getAge());
        user.setBvn(registrationRequest.getBvn());
        user.setNin(registrationRequest.getNin());
        user.setEmail(registrationRequest.getEmail());
        user.setOccupation(registrationRequest.getOccupation());
        user.setOtherName(registrationRequest.getOtherName());
        user.setNextOfKinFirstName(registrationRequest.getNextOfKinFirstName());
        user.setNextOfKinLastName(registrationRequest.getNextOfKinLastName());
        user.setNextOfKinOccupation(registrationRequest.getNextOfKinOccupation());
        user.setNextOfKinAddress(registrationRequest.getNextOfKinAddress());

        // Encrypt the password before saving the user
        user.setPassword(passwordEncoderUtil.encodePassword(registrationRequest.getPassword()));

        user.setRoles(Set.of(Role.USER)); // Assigning the USER role to the new user
        // Save the user to the database
        userService.save(user);

        // Return a success response
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CustomResponse.success(Optional.of("User registered successfully"), "Registration successful")
        );
    }
}
