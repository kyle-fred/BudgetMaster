package com.budgetmaster.common.dto;

import com.budgetmaster.common.dto.builder.ErrorResponseBuilder;
import com.budgetmaster.common.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final ErrorCode errorCode;
    private final String message;
    private final String path;
    private final List<ValidationError> errors;

    public ErrorResponse(ErrorResponseBuilder builder) {
        this.timestamp = builder.timestamp;
        this.status = builder.status;
        this.errorCode = builder.errorCode;
        this.message = builder.message;
        this.path = builder.path;
        this.errors = builder.errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
} 