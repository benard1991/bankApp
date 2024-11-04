package com.bankApp.exceptionHandler;

import lombok.Data;

import java.time.LocalDateTime;


public class ErrorResponse {

    private int statusCode;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }


}
