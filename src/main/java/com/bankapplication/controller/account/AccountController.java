package com.bankapplication.controller.account;

import com.bankapplication.dto.AccountDto;
import com.bankapplication.dto.GenericResponse;
import com.bankapplication.model.Account;
import com.bankapplication.service.accountService.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<GenericResponse<List<Account>>> getAccountsByUserId(@PathVariable @Valid Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(new GenericResponse<>(accounts, "Accounts retrieved successfully", HttpStatus.OK.value()));
    }


    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/{accountNumber}")
    public ResponseEntity<GenericResponse<Account>> getAccountByAccountNumber(@PathVariable String accountNumber) {
        Optional<Account> account = accountService.getAccountByAccountNumber(accountNumber);
        GenericResponse<Account> response = new GenericResponse<>(account.get(), "Account retrieved successfully", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }


}



