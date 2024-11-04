package com.bankApp.exceptionHandler;

public class UserNotFoundExeption  extends RuntimeException{

    public UserNotFoundExeption(String message){
        super(message);
    }
}
