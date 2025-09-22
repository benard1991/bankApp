package com.bankapplication.controller.transaction;

import com.bankapplication.controller.account.AccountController;
import com.bankapplication.dto.DepositRequest;
import com.bankapplication.dto.TransactionDto;
import com.bankapplication.dto.TransactionResponseDto;
import com.bankapplication.model.Account;
import com.bankapplication.service.JwtService;
import com.bankapplication.service.MyUserDetailsService;
import com.bankapplication.service.UserRateLimiterService;
import com.bankapplication.service.accountService.AccountService;
import com.bankapplication.service.transactionService.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Mock
    private  AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private JwtService jwtService;
    @MockBean private MyUserDetailsService userDetailsService;
    @MockBean private UserRateLimiterService rateLimiterService;

    @Test
    @WithMockUser(username = "customer", authorities = {"ROLE_CUSTOMER"})
    void processTransaction_shouldReturnSuccess_whenRequestIsValid() throws Exception {
        TransactionDto request = new TransactionDto();
        request.setAmount(5000.0);
        request.setUserId(2L);
        request.setSourceAccountNumber("8455735209");
        request.setDestinationAccountNumber("8455735209");
        request.setTransferChannel("INTRA");

        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setTransferChannel("INTRA");
        responseDto.setAmount(5000.0);
        responseDto.setStatus("200");
        responseDto.setSourceAccount("8455735209");
        responseDto.setDestinationAccount("8455735209");

        when(transactionService.handleTransaction(Mockito.any(TransactionDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction processed successfully"))
                .andExpect(jsonPath("$.data.amount").value(5000.0))
                .andExpect(jsonPath("$.data.sourceAccount").value("8455735209"))
                .andExpect(jsonPath("$.data.destinationAccount").value("8455735209"))
                .andExpect(jsonPath("$.data.status").value("200"));
    }


    @Test
    @WithMockUser(authorities = "ROLE_CUSTOMER")
    void deposit_shouldReturnSuccessResponse() throws Exception {
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountNumber("1234567890");
        depositRequest.setAmount(1000.0);

        Account mockAccount = new Account();
        mockAccount.setAccountNumber("1234567890");
        mockAccount.setBalance(5000.0);

        Mockito.when(transactionService.deposit("1234567890", 1000.0)).thenReturn(mockAccount);

        mockMvc.perform(post("/api/v1/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deposit successful"))
                .andExpect(jsonPath("$.data.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.data.balance").value(5000.0));
    }

    @Test
    @WithMockUser(authorities = "ROLE_CUSTOMER")
    void withdraw_shouldReturnSuccessResponse() throws Exception {
        DepositRequest withdrawRequest = new DepositRequest();
        withdrawRequest.setAccountNumber("9876543210");
        withdrawRequest.setAmount(500.0);

        Account mockAccount = new Account();
        mockAccount.setAccountNumber("9876543210");
        mockAccount.setBalance(2500.0);

        Mockito.when(transactionService.withdraw("9876543210", 500.0)).thenReturn(mockAccount);

        mockMvc.perform(post("/api/v1/transaction/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("withdrawal successful"))
                .andExpect(jsonPath("$.data.accountNumber").value("9876543210"))
                .andExpect(jsonPath("$.data.balance").value(2500.0));
    }




}