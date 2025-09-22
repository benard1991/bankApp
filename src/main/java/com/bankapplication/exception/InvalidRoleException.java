package com.bankapplication.exception;

public class InvalidRoleException extends RuntimeException{

    // Constructor with message
    public InvalidRoleException(String message) {
        super("Role policy is required."); // Pass the message to the parent (RuntimeException)
    }

    // Optional: Constructor with message and cause (for chaining exceptions)
    public InvalidRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}
