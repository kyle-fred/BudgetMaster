package com.budgetmaster.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import com.budgetmaster.common.enums.ErrorCode;
import com.budgetmaster.exception.dto.ErrorResponse;
import com.budgetmaster.exception.dto.ValidationError;
import com.budgetmaster.testsupport.constants.ExceptionTest;
import com.budgetmaster.testsupport.constants.Paths;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

@SuppressWarnings("null") // We are explicitly testing validation error handling which may involve nulls
public class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;
    
    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn(Paths.Endpoints.TEST);
    }
    
    @Test
    void handleValidationExceptions_WithFieldErrors_ReturnsBadRequestWithValidationErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError(
            ExceptionTest.Validation.OBJECT_NAME, 
            ExceptionTest.Validation.FIELD_NAME, 
            ExceptionTest.Validation.ERROR_MESSAGE
        );
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(ex, webRequest);
        
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR);
        assertValidationError(
            response.getBody().getErrors().get(0), 
            ExceptionTest.Validation.FIELD_NAME, 
            ExceptionTest.Validation.ERROR_MESSAGE
        );
    }
    
    @Test
    void handleConstraintViolation_WithViolations_ReturnsBadRequestWithValidationErrors() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        
        when(ex.getConstraintViolations()).thenReturn(Set.of(violation));
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn(ExceptionTest.Validation.FIELD_PATH);
        when(violation.getMessage()).thenReturn(ExceptionTest.Validation.ERROR_MESSAGE);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolation(ex, webRequest);
        
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR);
        assertValidationError(
            response.getBody().getErrors().get(0), 
            ExceptionTest.Validation.FIELD_NAME, 
            ExceptionTest.Validation.ERROR_MESSAGE
        );
    }
    
    @Test
    void handleInvalidRequest_WithValueInstantiationException_ReturnsBadRequest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        ValueInstantiationException valueEx = mock(ValueInstantiationException.class);
        JsonMappingException.Reference reference = mock(JsonMappingException.Reference.class);
        Object from = new Object();
        
        when(ex.getCause()).thenReturn(valueEx);
        when(valueEx.getPath()).thenReturn(List.of(reference));
        when(reference.getFieldName()).thenReturn(ExceptionTest.Validation.FIELD_NAME);
        when(reference.getFrom()).thenReturn(from);
        when(valueEx.getMessage()).thenReturn(ExceptionTest.Enum.INVALID_VALUE_MESSAGE);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidRequest(ex, webRequest);
        
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ENUM_VALUE);
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleGenericException_ReturnsInternalServerError() {
        Exception ex = new Exception(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex, webRequest);
        
        assertErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR);
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleDataIntegrityViolation_ReturnsConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(ErrorCode.DATABASE_ERROR.getMessage());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(ex, webRequest);
        
        assertErrorResponse(response, HttpStatus.CONFLICT, ErrorCode.DATABASE_ERROR);
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleBudgetNotFound_ReturnsNotFound() {
        String errorMessage = ErrorCode.RESOURCE_NOT_FOUND.getMessage();
        BudgetNotFoundException ex = new BudgetNotFoundException(errorMessage);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBudgetNotFound(ex, webRequest);
        
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, errorMessage);
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleIncomeNotFound_ReturnsNotFound() {
        String errorMessage = ErrorCode.RESOURCE_NOT_FOUND.getMessage();
        IncomeNotFoundException ex = new IncomeNotFoundException(errorMessage);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIncomeNotFound(ex, webRequest);
        
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, errorMessage);
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleExpenseNotFound_ReturnsNotFound() {
        String errorMessage = ErrorCode.RESOURCE_NOT_FOUND.getMessage();
        ExpenseNotFoundException ex = new ExpenseNotFoundException(errorMessage);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleExpenseNotFound(ex, webRequest);
        
        assertErrorResponse(response, HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, errorMessage);
        assertTrue(response.getBody().getErrors().isEmpty());
    }

    // -- Helper methods --

    private void assertErrorResponse(ResponseEntity<ErrorResponse> response, HttpStatus expectedStatus, ErrorCode expectedErrorCode) {
        assertErrorResponse(response, expectedStatus, expectedErrorCode, expectedErrorCode.getMessage());
    }

    private void assertErrorResponse(ResponseEntity<ErrorResponse> response, HttpStatus expectedStatus, ErrorCode expectedErrorCode, String expectedMessage) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(expectedStatus.value(), response.getBody().getStatus());
        assertEquals(expectedErrorCode, response.getBody().getErrorCode());
        assertEquals(expectedMessage, response.getBody().getMessage());
        assertEquals(Paths.Endpoints.TEST, response.getBody().getPath());
        assertNotNull(response.getBody().getErrors());
    }

    private void assertValidationError(ValidationError validationError, String expectedField, String expectedMessage) {
        assertNotNull(validationError);
        assertEquals(expectedField, validationError.getField());
        assertEquals(expectedMessage, validationError.getMessage());
    }
} 