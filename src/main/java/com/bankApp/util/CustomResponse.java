package com.bankApp.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse<T> {
    private T data;
    private Integer statusCode;
    private String message;

    public CustomResponse(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public CustomResponse(T data, Integer statusCode, String message) {
        this.data = data;
        this.statusCode = statusCode;
        this.message = message;
    }
}
