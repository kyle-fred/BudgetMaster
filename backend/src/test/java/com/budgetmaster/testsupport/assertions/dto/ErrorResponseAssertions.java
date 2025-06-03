package com.budgetmaster.testsupport.assertions.dto;

import com.budgetmaster.application.exception.dto.ErrorResponse;
import com.budgetmaster.application.exception.dto.ValidationError;
import com.budgetmaster.testsupport.constants.PathConstants;
import com.budgetmaster.application.exception.codes.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("null") // We are explicitly testing validation error handling which may involve nulls
public class ErrorResponseAssertions {

    private final ResponseEntity<ErrorResponse> actualResponse;

    private ErrorResponseAssertions(ResponseEntity<ErrorResponse> actualResponse) {
        this.actualResponse = actualResponse;
    }

    public static ErrorResponseAssertions assertErrorResponse(ResponseEntity<ErrorResponse> response) {
        assertThat(response).isNotNull();
        return new ErrorResponseAssertions(response);
    }

    public ErrorResponseAssertions hasErrorResponseSkeleton() {
        assertThat(actualResponse.getBody()).isNotNull();
        assertThat(actualResponse.getBody().getTimestamp()).isNotNull();
        assertThat(actualResponse.getBody().getStatus()).isNotNull();
        assertThat(actualResponse.getBody().getErrorCode()).isNotNull();
        assertThat(actualResponse.getBody().getMessage()).isNotNull();
        assertThat(actualResponse.getBody().getPath()).isNotNull();
        assertThat(actualResponse.getBody().getErrors()).isNotNull();
        return this;
    }

    public ErrorResponseAssertions hasStatus(HttpStatus expectedStatus) {
        assertThat(actualResponse.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(actualResponse.getBody().getStatus()).isEqualTo(expectedStatus.value());
        return this;
    }

    public ErrorResponseAssertions hasErrorCode(ErrorCode expectedErrorCode) {
        assertThat(actualResponse.getBody().getErrorCode()).isEqualTo(expectedErrorCode);
        return this;
    }

    public ErrorResponseAssertions hasMessage(String expectedMessage) {
        assertThat(actualResponse.getBody().getMessage()).isEqualTo(expectedMessage);
        return this;
    }

    public ErrorResponseAssertions hasPath(String expectedPath) {
        assertThat(actualResponse.getBody().getPath()).isEqualTo(expectedPath);
        return this;
    }

    public ErrorResponseAssertions hasNoValidationErrors() {
        assertThat(actualResponse.getBody().getErrors()).isEmpty();
        return this;
    }

    public ErrorResponseAssertions hasValidationError(String expectedField, String expectedMessage) {
        assertThat(actualResponse.getBody().getErrors()).isNotEmpty();
        ValidationError validationError = actualResponse.getBody().getErrors().get(0);
        assertThat(validationError.getField()).isEqualTo(expectedField);
        assertThat(validationError.getMessage()).isEqualTo(expectedMessage);
        return this;
    }

    public ErrorResponseAssertions matchesValidationError(String expectedField, String expectedMessage) {
        return hasErrorResponseSkeleton()
            .hasStatus(HttpStatus.BAD_REQUEST)
            .hasErrorCode(ErrorCode.VALIDATION_ERROR)
            .hasMessage(ErrorCode.VALIDATION_ERROR.getMessage())
            .hasPath(PathConstants.Endpoints.TEST)
            .hasValidationError(expectedField, expectedMessage);
    }

    public ErrorResponseAssertions matchesInvalidEnumValue() {
        return hasErrorResponseSkeleton()
            .hasStatus(HttpStatus.BAD_REQUEST)
            .hasErrorCode(ErrorCode.INVALID_ENUM_VALUE)
            .hasMessage(ErrorCode.INVALID_ENUM_VALUE.getMessage())
            .hasPath(PathConstants.Endpoints.TEST)
            .hasNoValidationErrors();
    }

    public ErrorResponseAssertions matchesInternalServerError() {
        return hasErrorResponseSkeleton()
            .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
            .hasErrorCode(ErrorCode.INTERNAL_SERVER_ERROR)
            .hasMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
            .hasPath(PathConstants.Endpoints.TEST)
            .hasNoValidationErrors();
    }

    public ErrorResponseAssertions matchesConflict() {
        return hasErrorResponseSkeleton()
            .hasStatus(HttpStatus.CONFLICT)
            .hasErrorCode(ErrorCode.DATABASE_ERROR)
            .hasMessage(ErrorCode.DATABASE_ERROR.getMessage())
            .hasPath(PathConstants.Endpoints.TEST)
            .hasNoValidationErrors();
    }

    public ErrorResponseAssertions matchesResourceNotFound() {
        return hasErrorResponseSkeleton()
            .hasStatus(HttpStatus.NOT_FOUND)
            .hasErrorCode(ErrorCode.RESOURCE_NOT_FOUND)
            .hasMessage(ErrorCode.RESOURCE_NOT_FOUND.getMessage())
            .hasPath(PathConstants.Endpoints.TEST)
            .hasNoValidationErrors();
    }
}
