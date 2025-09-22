package com.bankapplication.exception;

public class DuplicateAccountTypeException extends RuntimeException{
    public DuplicateAccountTypeException(String message) {
        super(message);
    }
    public DuplicateAccountTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
