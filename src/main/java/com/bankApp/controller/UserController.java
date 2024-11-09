package com.bankApp.controller;

import com.bankApp.dto.RegistrationRequest;
import com.bankApp.exceptionHandler.BvnExistException;
import com.bankApp.exceptionHandler.EmailAlreadyExistsException;
import com.bankApp.exceptionHandler.NinExistException;
import com.bankApp.model.Account;
import com.bankApp.model.Role;
import com.bankApp.model.User;
import com.bankApp.security.JwtUtil;
import com.bankApp.security.PasswordEncoderUtil;
import com.bankApp.services.UserService;
import com.bankApp.util.CustomResponse;
import com.bankApp.util.AccountNumberGenerator;
import com.bankApp.util.ValidationErrorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Autowired
    private ValidationErrorService validationErrorService;

    @PostMapping("/register")
    public ResponseEntity<CustomResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            CustomResponse<List<String>> errorResponse = validationErrorService.getErrorResponse(bindingResult);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        // Check if email already exists
        if (userService.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
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
        String accountNumber = AccountNumberGenerator.generateAccountNumber();  // Generate account number
        account.setAccountNumber(accountNumber);  // Set the generated account number
        account.setBalance(0.0);
        account.setAccountType(registrationRequest.getAccountType());

        System.out.println("Generated Account Number: " + accountNumber); // Debugging log
        // Create the user and set necessary fields
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
        // Encrypt the password
        user.setPassword(passwordEncoderUtil.encodePassword(registrationRequest.getPassword()));
        // Assign roles to the user
        user.setRoles(Set.of(Role.USER));
        // Set the generated account to the user
        user.setAccount(account);
        account.setUser(user);  // Set the user
        // Save the user and cascade the account save
        userService.save(user);
        // Return response
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResponse<>(HttpStatus.CREATED.value(), "User successfully registered"));
    }


}
