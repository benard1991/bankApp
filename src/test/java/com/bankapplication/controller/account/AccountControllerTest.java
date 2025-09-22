package com.bankapplication.controller.account;

import com.bankapplication.dto.AccountDto;
import com.bankapplication.model.Account;
import com.bankapplication.model.User;
import com.bankapplication.model.enums.AccountType;
import com.bankapplication.service.JwtService;
import com.bankapplication.service.MyUserDetailsService;
import com.bankapplication.service.UserRateLimiterService;
import com.bankapplication.service.accountService.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;


@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable filters to skip JwtFilter if needed
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean private JwtService jwtService;
    @MockBean private MyUserDetailsService userDetailsService;
    @MockBean private UserRateLimiterService rateLimiterService;

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_CUSTOMER"})
    void createAccount_shouldReturnCreatedAccount_WhenRequestIsValid() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountType(AccountType.valueOf("SAVINGS"));
        accountDto.setBalance(1000.0);
        accountDto.setUserId(1L);

        Account createdAccount = new Account();
        createdAccount.setId(1L);
        createdAccount.setAccountNumber("1234567890");
        createdAccount.setBalance(1000.0);
        createdAccount.setAccountType(AccountType.valueOf("SAVINGS"));

        when(accountService.createAccount(any(AccountDto.class))).thenReturn(createdAccount);

        mockMvc.perform(post("/api/v1/account/createAccount")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(accountDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Account created successfully"))
                .andExpect(jsonPath("$.data.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.data.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.data.balance").value(1000.0));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_CUSTOMER"})
    void createAccount_shouldReturn400_WhenUserIdIsMissing() throws Exception {
        AccountDto dto = new AccountDto();
        dto.setAccountNumber("1234567890");
        dto.setAccountType(AccountType.valueOf("SAVINGS"));
        dto.setBalance(1000.0);
        dto.setUserId(null);

        mockMvc.perform(post("/api/v1/account/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())  // Expect 400 due to @Valid failure
                .andExpect(jsonPath("$.userId").value("User ID cannot be null")); // Match validation error message
    }


    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_CUSTOMER"})
    void getAccountsByUserId_shouldReturnAccounts_WhenUserIsCustomer() throws Exception {
        Long userId = 1L;
        List<Account> accounts = List.of(
                new Account(1L, "1234567890", 1000.0, null),
                new Account(2L, "0987654321", 2000.0, null)
        );

        when(accountService.getAccountsByUserId(userId)).thenReturn(accounts);

        mockMvc.perform(get("/api/v1/account/{userId}/accounts", userId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Accounts retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_CUSTOMER"})
    void getAccountByAccountNumber_shouldReturnAccount_WhenUserIsCustomer() throws Exception {
        String accountNumber = "1234567890";
        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber(accountNumber);
        account.setBalance(1000.0);
        account.setAccountType(AccountType.valueOf("SAVINGS"));

        when(accountService.getAccountByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        mockMvc.perform(get("/api/v1/account/{accountNumber}", accountNumber)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account retrieved successfully"))
                .andExpect(jsonPath("$.data.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.data.balance").value(1000.0));
    }



}
