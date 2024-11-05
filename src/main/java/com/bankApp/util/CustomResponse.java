package com.bankApp.util;


import java.util.Optional;
import org.springframework.http.HttpStatus;

public class CustomResponse<T> {
    private Optional<T> data;
    private String status;
    private int statusCode;
    private String message;

    // Constructor
    public CustomResponse(Optional<T> data, String status, int statusCode, String message) {
        this.data = data;
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
    }

    // Getters and Setters
    public Optional<T> getData() {
        return data;
    }

    public void setData(Optional<T> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Static factory methods for convenience
    public static <T> CustomResponse<T> success(Optional<T> data, String message) {
        return new CustomResponse<>(data,HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), message);
    }

    public static <T> CustomResponse<T> error(String message, int statusCode) {
        return new CustomResponse<>(Optional.empty(), HttpStatus.OK.getReasonPhrase(), statusCode, message);
    }
}
