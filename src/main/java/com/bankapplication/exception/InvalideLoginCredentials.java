package com.bankapplication.exception;

public class InvalideLoginCredentials extends RuntimeException{

    public InvalideLoginCredentials() {
        super("User already exists!");
    }

    // Optional: keep this if you want a custom message too
    public InvalideLoginCredentials(String message) {
        super(message);
    }
}
