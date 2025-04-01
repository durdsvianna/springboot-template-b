package com.example.customerdatasync.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorDetails extends ErrorDetails {
    private Map<String, String> validationErrors;

    public ValidationErrorDetails(LocalDateTime timestamp, String message, Map<String, String> validationErrors, String errorCode) {
        super(timestamp, message, "Validation Failed", errorCode);
        this.validationErrors = validationErrors;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
} 