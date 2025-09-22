package com.bankapplication.exception;

public class InvalidNextOfKinDetailsException extends RuntimeException{


    public InvalidNextOfKinDetailsException(String message) {
        super(message);
    }

    public InvalidNextOfKinDetailsException(String message, Throwable cause) {
        super(message, cause);
    }
}
