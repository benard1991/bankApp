package com.bankapplication.exception;

public class InvalidAccountTypeException extends  RuntimeException{

    public InvalidAccountTypeException(String message) {
        super(message);
    }
    public InvalidAccountTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
