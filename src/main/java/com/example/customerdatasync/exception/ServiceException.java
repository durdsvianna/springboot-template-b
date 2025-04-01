package com.example.customerdatasync.exception;

public class ServiceException extends RuntimeException {
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceException(String errorMessage, String operationName, Throwable cause) {
        super(String.format("Error during %s operation: %s", operationName, errorMessage), cause);
    }
} 