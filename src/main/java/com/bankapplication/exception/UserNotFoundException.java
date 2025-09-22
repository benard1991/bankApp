package com.bankapplication.exception;

public class UserNotFoundException extends  RuntimeException {

    // Constructor accepting message
    public UserNotFoundException(String message) {
        super(message);
    }

    // Constructor accepting both message and cause
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
