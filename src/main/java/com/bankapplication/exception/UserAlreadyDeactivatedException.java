package com.bankapplication.exception;

public class UserAlreadyDeactivatedException extends  RuntimeException{

    public UserAlreadyDeactivatedException(String message) {
        super(message); // Use the passed message
    }

    public UserAlreadyDeactivatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
