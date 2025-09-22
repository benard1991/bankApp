package com.bankapplication.service.accountService;


import com.bankapplication.dto.AccountDto;
import com.bankapplication.model.Account;
import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account createAccount(AccountDto accountDto);

    Optional<Account> findById(Long userId);

    List<Account> getAccountsByUserId(Long userId);

    Optional<Account> getAccountByAccountNumber(String accountNumber);




}
