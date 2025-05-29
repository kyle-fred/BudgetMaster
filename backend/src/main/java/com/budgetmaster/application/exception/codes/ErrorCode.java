package com.budgetmaster.application.exception.codes;

public enum ErrorCode {
    // Resource not found errors
    RESOURCE_NOT_FOUND("Resource not found"),
    
    // Validation errors
    VALIDATION_ERROR("Validation error"),
    INVALID_INPUT("Invalid input"),
    INVALID_ENUM_VALUE("Invalid enum value"),
    
    // System errors
    INTERNAL_SERVER_ERROR("Internal server error"),
    SYNCHRONIZATION_FAILED("Synchronization failed"),
    DATABASE_ERROR("Database error");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
} 