package com.bankApp.exceptionHandler;

public class EmailNotFoundException extends RuntimeException{

    public EmailNotFoundException(String message) {
        super(message);
    }
}
