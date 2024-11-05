package com.bankApp.exceptionHandler;

public class BvnExistException extends  RuntimeException{

    public BvnExistException(String message){
        super(message);
    }
}
