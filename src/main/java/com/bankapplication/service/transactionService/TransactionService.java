package com.bankapplication.service.transactionService;

import com.bankapplication.dto.TransactionDto;
import com.bankapplication.dto.TransactionResponseDto;
import com.bankapplication.model.Account;
import com.bankapplication.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    TransactionResponseDto handleTransaction(TransactionDto transactionRequestDto,HttpServletRequest request);

    public Account deposit(String accountNumber, double amount, HttpServletRequest request);

    public Account withdraw(String accountNumber, double amount, HttpServletRequest request);

    public Page<Transaction> getTransactions(Pageable pageable);

    Page<Transaction>getUserTransaction(Long userId, Pageable pageable);


}
