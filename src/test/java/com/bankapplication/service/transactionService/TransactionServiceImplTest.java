package com.bankapplication.service.transactionService;

import com.bankapplication.dto.TransactionDto;
import com.bankapplication.dto.TransactionResponseDto;
import com.bankapplication.exception.AccountNotFoundException;
import com.bankapplication.exception.UserAlreadyExistsException;
import com.bankapplication.model.Account;
import com.bankapplication.model.User;
import com.bankapplication.repository.AccountRepository;
import com.bankapplication.repository.TransactionRepository;
import com.bankapplication.service.accountService.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private com.bankapplication.service.util.TransactionUtils transactionUtils;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        account = new Account();
        account.setUser(user);
        account.setAccountNumber("1234567890");
        account.setBalance(1000.0);
    }

    @Test
    void deposit_shouldUpdateBalanceAndSaveTransaction_WhenAccountExists() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(transactionUtils).createTransaction(
                any(Account.class), eq(1000.0), eq("CREDIT"),
                eq(account.getAccountNumber()), isNull(), eq("DEPOSIT"), isNull(), eq("SUCCESS")
        );

        Account updatedAccount = transactionService.deposit(account.getAccountNumber(), 1000.0);

        assertEquals(2000.0, updatedAccount.getBalance());
        verify(accountRepository).findByAccountNumber(account.getAccountNumber());
        verify(accountRepository).save(account);
    }

    @Test
    void deposit_shouldSucceed_WhenValidInput() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(transactionUtils).createTransaction(
                any(Account.class), eq(100.0), eq("CREDIT"),
                eq(account.getAccountNumber()), isNull(), eq("DEPOSIT"), isNull(), eq("SUCCESS")
        );

        Account updatedAccount = transactionService.deposit(account.getAccountNumber(), 100.0);

        assertEquals(1100.0, updatedAccount.getBalance());
        verify(transactionUtils).createTransaction(
                any(Account.class), eq(100.0), eq("CREDIT"),
                eq(account.getAccountNumber()), isNull(), eq("DEPOSIT"), isNull(), eq("SUCCESS")
        );
        verify(accountRepository).save(account);
    }

    @Test
    void withdraw_shouldSucceed_WhenValidInput() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(transactionUtils).createTransaction(
                any(Account.class), eq(500.0), eq("CREDIT"),
                eq(account.getAccountNumber()), isNull(), eq("WITHDRAWAL"), isNull(), eq("SUCCESS")
        );

        Account updatedAccount = transactionService.withdraw(account.getAccountNumber(), 500.0);

        assertEquals(500.0, updatedAccount.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void withdraw_shouldThrowException_WhenAccountNotFound() {
        String missingAccount = "0000000000";

        when(accountRepository.findByAccountNumber(missingAccount)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                transactionService.withdraw(missingAccount, 100.0));

        verify(accountRepository).findByAccountNumber(missingAccount);
    }

    @Test
    void handleTransaction_shouldSucceed_ForIntraTransfer() {
        TransactionDto dto = new TransactionDto();
        dto.setAmount(500.0);
        dto.setSourceAccountNumber("1111");
        dto.setDestinationAccountNumber("2222");
        dto.setTransferChannel("INTRA");

        Account source = new Account();
        source.setAccountNumber("1111");
        source.setBalance(1000.0);

        Account destination = new Account();
        destination.setAccountNumber("2222");
        destination.setBalance(500.0);

        when(accountRepository.findByAccountNumber("1111")).thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("2222")).thenReturn(Optional.of(destination));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(transactionUtils).createTransaction(any(), anyDouble(), any(), any(), any(), any(), any(), any());

        TransactionResponseDto response = transactionService.handleTransaction(dto);

        assertEquals("DEBIT", response.getTransactionType());
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void handleTransaction_shouldFail_WhenAmountIsInvalid() {
        TransactionDto dto = new TransactionDto();
        dto.setAmount(0.0);
        dto.setSourceAccountNumber("1111");
        dto.setTransferChannel("INTRA");

        Exception ex = assertThrows(UserAlreadyExistsException.InvalidAmountException.class,
                () -> transactionService.handleTransaction(dto));

        assertEquals("Deposit amount must be greater than zero.", ex.getMessage());
    }
}
