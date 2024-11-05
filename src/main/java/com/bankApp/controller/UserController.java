package com.bankApp.controller;

import com.bankApp.dto.RegistrationRequest;
import com.bankApp.exceptionHandler.BvnExistException;
import com.bankApp.exceptionHandler.EmailNotFoundException;
import com.bankApp.exceptionHandler.NinExistException;
import com.bankApp.model.Account;
import com.bankApp.model.User;
import com.bankApp.security.JwtUtil;
import com.bankApp.security.PasswordEncoderUtil;
import com.bankApp.services.UserService;
import com.bankApp.util.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bankApp.util.AccountNumberGenerator.generateAccountNumber;

@RestController
@RequestMapping("/api/")
public class UserController {

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private  UserService userService;
    @Autowired
    private Account account;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<?>register(@RequestBody RegistrationRequest registrationRequest) {
        if(userService.findByEmail(registrationRequest.getEmail()).isPresent()){
            throw  new EmailNotFoundException("Email already exist!");
        }
        if(userService.findByBvn(registrationRequest.getBvn()).isPresent()){
           throw new BvnExistException("BVN already exist!");
        }
        if(userService.findByNin(registrationRequest.getNin()).isPresent()){
           throw new NinExistException("NIN already exist");
        }

        Account account = new Account();
        long accountNumber = generateAccountNumber();
        account.setAccountNumber(accountNumber);
        User user = new User();
        user.setFirstName(registrationRequest.getFirstName());
        user.setPassword(passwordEncoderUtil.encodePassword(registrationRequest.getPassword()));
        user.setLastName(registrationRequest.getLastName());
        user.setAddress(registrationRequest.getAddress());
        user.setAge(registrationRequest.getAge());
        user.setBvn(registrationRequest.getBvn());
        user.setNin(registrationRequest.getNin());
        user.setEmail(registrationRequest.getEmail());
        user.setOccupation(registrationRequest.getOccupation());
        user.setOtherName(registrationRequest.getOtherName());
        user.setNextOfKinAddress(registrationRequest.getNextOfKinAddress());
        user.setNextOfKinFirstName(registrationRequest.getNextOfKinFirstName());
        user.setNextOfKinLastName(registrationRequest.getNextOfKinLastName());
        user.setNextOfKinOccupation(registrationRequest.getNextOfKinOccupation());

        userService.save(user);

//        return ResponseEntity.ok().body();

        return ResponseEntity.ok().body("User registered successfully");


    }






}
