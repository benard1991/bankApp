package com.bankapplication.controller.account;

import com.bankapplication.dto.AccountDto;
import com.bankapplication.dto.GenericResponse;
import com.bankapplication.model.Account;
import com.bankapplication.service.accountService.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private  final AccountService accountService;

    public AccountController(AccountService accountSevice) {
        this.accountService = accountSevice;
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/createAccount")
    public ResponseEntity<GenericResponse<Account>> createAccount(@RequestBody @Valid AccountDto accountDto) {
        Account createdAccount = accountService.createAccount(accountDto);
        return ResponseEntity.status(201).body(new GenericResponse<>(createdAccount, "Account created successfully", 201));

    }


    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/{userId}/accounts")
    public ResponseEntity<GenericResponse<List<AccountDto>>> getAccountsByUserId(@PathVariable Long userId) {
        log.info("Fetching accounts for userId: {}", userId);
        try {
            List<AccountDto> accounts = accountService.getAccountsByUserId(userId);
            log.info("Accounts fetched successfully for userId: {}", userId);
            return ResponseEntity.ok(new GenericResponse<>(accounts, "Accounts retrieved successfully", HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching accounts for userId {}: {}", userId, e.getMessage(), e);
            throw e; // Let your global handler handle it
        }
    }



    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/{accountNumber}")
    public ResponseEntity<GenericResponse<AccountDto>> getAccountByAccountNumber(@PathVariable String accountNumber) {
        AccountDto accountDto = accountService.getAccountByAccountNumber(accountNumber);
        GenericResponse<AccountDto> response = new GenericResponse<>(accountDto, "Account retrieved successfully", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }



}



