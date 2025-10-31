package com.bankapplication.controller.transaction;

import com.bankapplication.dto.*;
import com.bankapplication.model.Account;
import com.bankapplication.model.Transaction;
import com.bankapplication.service.transactionService.TransactionService;
import com.bankapplication.util.PaginationInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/transfer")
    public ResponseEntity<GenericResponse<TransactionResponseDto>> processTransaction(
            @Valid @RequestBody TransactionDto transactionDto, HttpServletRequest request) {
        logger.info("Received transaction request: {}", transactionDto);

        TransactionResponseDto transactionResponse = transactionService.handleTransaction(transactionDto,request );
        GenericResponse<TransactionResponseDto> response = new GenericResponse<>(
                transactionResponse,
                "Transaction processed successfully",
                HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/deposit")
    public ResponseEntity<GenericResponse<Account>> deposit(@RequestBody DepositRequest depositRequest,HttpServletRequest request) {
        Account account = transactionService.deposit(depositRequest.getAccountNumber(), depositRequest.getAmount(),request);
        GenericResponse<Account> response = new GenericResponse<>(account, "Deposit successful", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/withdraw")
    public ResponseEntity<GenericResponse<Account>> withdraw(@RequestBody DepositRequest depositRequest, HttpServletRequest request) {
        Account account = transactionService.withdraw(depositRequest.getAccountNumber(), depositRequest.getAmount(),request);
        GenericResponse<Account> response = new GenericResponse<>(account, "withdrawal successful", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/getTransactions")
    public ResponseEntity<GenericResponse<CustomPageResponse>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    )
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Transaction> transactionPage = transactionService.getTransactions(pageable);

        PaginationInfo paginationInfo = new PaginationInfo(
                transactionPage.getSize(),
                transactionPage.getNumber(),
                transactionPage.getTotalPages(),
                transactionPage.getTotalElements()
        );
        CustomPageResponse response = new CustomPageResponse(transactionPage.getContent(), paginationInfo);
        return ResponseEntity.ok(new GenericResponse<>(response, "Users retrieved successfully", 200));

    }


    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/{userId}/getUserTransaction")
    public ResponseEntity<GenericResponse<CustomPageResponse>> getUserTransaction(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Transaction> transactionPage = transactionService.getUserTransaction(userId, pageable);

        PaginationInfo paginationInfo = new PaginationInfo(
                transactionPage.getSize(),
                transactionPage.getNumber(),
                transactionPage.getTotalPages(),
                transactionPage.getTotalElements()
        );

        CustomPageResponse response = new CustomPageResponse(transactionPage.getContent(), paginationInfo);

        return ResponseEntity.ok(new GenericResponse<>(response, "Transactions retrieved successfully", 200));
    }

}
