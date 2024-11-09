package com.bankApp.util;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValidationErrorService {

    // Method to get validation error messages
    public List<String> getErrorMessages(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
    }

    // Method to create a CustomResponse with validation error messages
    public CustomResponse<List<String>> getErrorResponse(BindingResult bindingResult) {
        List<String> errorMessages = getErrorMessages(bindingResult);
        return new CustomResponse<>(errorMessages, 400, "Validation errors");
    }
}