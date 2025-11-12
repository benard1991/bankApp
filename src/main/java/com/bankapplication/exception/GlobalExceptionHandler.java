package com.bankapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// GlobalExceptionHandler for handling custom exceptions globally
@ControllerAdvice
public class GlobalExceptionHandler {

    // Custom handler for UserAlreadyExistsException
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        // Create custom error details with numeric status code (409)
        ErrorDetails errorDetails = new ErrorDetails(409, ex.getMessage(), "CONFLICT");
        // Return response with HTTP status code 409 (CONFLICT)
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }


    // Custom handler for UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(404, ex.getMessage(), "NOT_FOUND");
        // Return response with HTTP status code 404 (NOT_FOUND)
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Custom handler for TokenExpiredException
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Object> handleTokenExpiredException(TokenExpiredException ex) {
        ErrorDetails errorDetails = new ErrorDetails(401, ex.getMessage(), "UNAUTHORIZED");
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED); //  Changed from NOT_FOUND to UNAUTHORIZED
    }

    // Custom handler for TokenExpiredException
    @ExceptionHandler(InvalideLoginCredentials.class)
    public ResponseEntity<Object> handleInvalideLoginCredentials(InvalideLoginCredentials ex) {
        ErrorDetails errorDetails = new ErrorDetails(401, ex.getMessage(), "UNAUTHORIZED");
        // Return response with HTTP status code 404 (NOT_FOUND)
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Custom handler for TokenNotFoundException
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<Object> handleTokenNotFoundException(TokenNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(401, ex.getMessage(), "UNAUTHORIZED");
        // Return response with HTTP status code 404 (NOT_FOUND)
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Custom handler for PasswordChangeException
    @ExceptionHandler(PasswordChangeException.class)
    public ResponseEntity<Object> handlePasswordChangeException(PasswordChangeException ex) {
        ErrorDetails errorDetails = new ErrorDetails(403, ex.getMessage(), "FORBIDDEN");
        // Return response with HTTP status code 404 (NOT_FOUND)
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }


    // Custom handler for AccountNotFoundException
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object> handleAccountNotFoundException(AccountNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(404, ex.getMessage(), "NOT FOUND");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Custom handler for AccountNotFoundException
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleInsufficientFundsException(InsufficientFundsException ex) {
        ErrorDetails errorDetails = new ErrorDetails(400, ex.getMessage(), "BAD_REQUEST");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Custom handler for AccountNotFoundException
    @ExceptionHandler(UserAlreadyActiveException.class)
    public ResponseEntity<Object> handleUserAlreadyActiveException(UserAlreadyActiveException ex) {
        ErrorDetails errorDetails = new ErrorDetails(400, ex.getMessage(), "BAD_REQUEST");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Custom handler for AccountNotFoundException
    @ExceptionHandler(UserAlreadyDeactivatedException.class)
    public ResponseEntity<Object> handleUserAlreadyActiveException(UserAlreadyDeactivatedException ex) {
        ErrorDetails errorDetails = new ErrorDetails(400, ex.getMessage(), "BAD_REQUEST");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Custom handler for InvalidAmountException
    @ExceptionHandler(UserAlreadyExistsException.InvalidAmountException.class)
    public ResponseEntity<Object> handleInvalidAmountException(UserAlreadyExistsException.InvalidAmountException ex) {
        ErrorDetails errorDetails = new ErrorDetails(400, ex.getMessage(), "BAD_REQUEST");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    //InvalidOtpException
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<Object> handleInvalidOtpException(InvalidOtpException ex) {
        ErrorDetails errorDetails = new ErrorDetails(404, ex.getMessage(), "FORBIDDEN");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    //InvalidAccountTypeException
    @ExceptionHandler(InvalidAccountTypeException.class)
    public ResponseEntity<Object> handleInvalidAccountTypeException(InvalidAccountTypeException ex) {
        ErrorDetails errorDetails = new ErrorDetails(400, ex.getMessage(), "BAD REQUEST");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    //AccountLimitExceededException
    @ExceptionHandler(AccountLimitExceededException.class)
    public ResponseEntity<Object> handleAccountLimitExceededException(AccountLimitExceededException ex) {
        ErrorDetails errorDetails = new ErrorDetails(403, ex.getMessage(), "FORBIDDEN");
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    //AccountLimitExceededException
    @ExceptionHandler(DuplicateAccountTypeException.class)
    public ResponseEntity<Object> handleDuplicateAccountTypeException(DuplicateAccountTypeException ex) {
        ErrorDetails errorDetails = new ErrorDetails(409, ex.getMessage(), "CONFLICT");
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    //AccountLimitExceededException
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<Object> handleImageUploadException(ImageUploadException ex) {
        ErrorDetails errorDetails = new ErrorDetails(500, ex.getMessage(), "INTERNAL_SERVER_ERROR");
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<String> handleDatabaseException(DatabaseException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGeneralException(Exception ex) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
//    }


    // Custom handler for MethodArgumentNotValidException (validation errors)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    public static class ErrorDetails {
        private int status;
        private String message;
        private String error;

        public ErrorDetails(int status, String message, String error) {
            this.status = status;
            this.message = message;
            this.error = error;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getError() {
            return error;
        }
    }
}
