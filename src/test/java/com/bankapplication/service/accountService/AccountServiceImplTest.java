package com.bankapplication.service.accountService;

import com.bankapplication.exception.AccountNotFoundException;
import com.bankapplication.exception.UserNotFoundException;
import com.bankapplication.model.Account;
import com.bankapplication.repository.AccountRepository;
import com.bankapplication.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    class AccountServiceImplTest {

        @Mock
        private AccountRepository accountRepository;

         @Mock
         private TransactionRepository transactionRepository;

        @InjectMocks
        private AccountServiceImpl accountService;


        @Test
        void getAccountsByUserId_shouldReturnAccounts_WhenAccountsExist() {
            Long userId = 1L;

            Account account1 = new Account();
            account1.setAccountNumber("1234567890");

            Account account2 = new Account();
            account2.setAccountNumber("9876543210");

            List<Account> mockAccounts = List.of(account1, account2);

            when(accountRepository.findByUserId(userId)).thenReturn(mockAccounts);

            List<Account> result = accountService.getAccountsByUserId(userId);

            assertEquals(2, result.size());
            assertEquals("1234567890", result.get(0).getAccountNumber());
            verify(accountRepository).findByUserId(userId);
        }

        @Test
        void getAccountsByUserId_shouldThrowException_WhenNoAccountsFound() {
            Long userId = 2L;

            when(accountRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
                accountService.getAccountsByUserId(userId);
            });

            System.out.println("Exception == "+exception.getMessage());
            assertEquals("No accounts found for userId: " + userId, exception.getMessage());
            verify(accountRepository).findByUserId(userId);
        }


    @Test
    void getAccountByAccountNumber_shouldReturnAccount_WhenExists() {
        String accountNumber = "1234567890";

        Account mockAccount = new Account();
        mockAccount.setAccountNumber(accountNumber);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(mockAccount));

        Optional<Account> result = accountService.getAccountByAccountNumber(accountNumber);

        assertTrue(result.isPresent());
        assertEquals(accountNumber, result.get().getAccountNumber());
        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void getAccountByAccountNumber_shouldThrowException_WhenNotFound() {
        String accountNumber = "0000000000";

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountByAccountNumber(accountNumber);
        });

        System.out.println("Exception === "+exception.getMessage());
        assertEquals("Account not found for account number: " + accountNumber, exception.getMessage());
        verify(accountRepository).findByAccountNumber(accountNumber);
    }


    }
