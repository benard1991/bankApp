package com.bankapplication.exception;

public class UserAlreadyActiveException extends  RuntimeException{

    public UserAlreadyActiveException(String message) {
        super(message);
    }

    public UserAlreadyActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
