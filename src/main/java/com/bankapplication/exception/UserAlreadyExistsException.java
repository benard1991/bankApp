package com.bankapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User with this email already exists.")


public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User already exists!");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public static class InvalidAmountException extends RuntimeException{
        public InvalidAmountException(String message) {
            super(message);
        }

        public InvalidAmountException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}