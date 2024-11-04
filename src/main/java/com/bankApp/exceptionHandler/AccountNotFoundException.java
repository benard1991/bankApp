package com.bankApp.exceptionHandler;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message){
       super(message);
    }
}
