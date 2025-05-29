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

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.common.dto.ErrorResponse;
import com.budgetmaster.common.enums.ErrorCode;
import com.budgetmaster.expense.exception.ExpenseNotFoundException;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.budgetmaster.testsupport.constants.Paths;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

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
        FieldError fieldError = new FieldError("object", "field", "error message");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(ex, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals(ErrorCode.VALIDATION_ERROR, response.getBody().getErrorCode());
        assertEquals(ErrorCode.VALIDATION_ERROR.getMessage(), response.getBody().getMessage());
        assertEquals(Paths.Endpoints.TEST, response.getBody().getPath());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("field", response.getBody().getErrors().get(0).getField());
        assertEquals("error message", response.getBody().getErrors().get(0).getMessage());
    }
    
    @Test
    void handleConstraintViolation_WithViolations_ReturnsBadRequestWithValidationErrors() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        
        when(ex.getConstraintViolations()).thenReturn(Set.of(violation));
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("object.field");
        when(violation.getMessage()).thenReturn("error message");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolation(ex, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals(ErrorCode.VALIDATION_ERROR, response.getBody().getErrorCode());
        assertEquals(ErrorCode.VALIDATION_ERROR.getMessage(), response.getBody().getMessage());
        assertEquals(Paths.Endpoints.TEST, response.getBody().getPath());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("field", response.getBody().getErrors().get(0).getField());
        assertEquals("error message", response.getBody().getErrors().get(0).getMessage());
    }
    
    @Test
    void handleInvalidRequest_WithValueInstantiationException_ReturnsBadRequest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        ValueInstantiationException valueEx = mock(ValueInstantiationException.class);
        JsonMappingException.Reference reference = mock(JsonMappingException.Reference.class);
        Object from = new Object();
        
        when(ex.getCause()).thenReturn(valueEx);
        when(valueEx.getPath()).thenReturn(List.of(reference));
        when(reference.getFieldName()).thenReturn("field");
        when(reference.getFrom()).thenReturn(from);
        when(valueEx.getMessage()).thenReturn("Invalid enum value: INVALID");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidRequest(ex, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals(ErrorCode.INVALID_ENUM_VALUE, response.getBody().getErrorCode());
        assertEquals(ErrorCode.INVALID_ENUM_VALUE.getMessage(), response.getBody().getMessage());
        assertEquals(Paths.Endpoints.TEST, response.getBody().getPath());
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleGenericException_ReturnsInternalServerError() {
        Exception ex = new Exception(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex, webRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, response.getBody().getErrorCode());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), response.getBody().getMessage());
        assertEquals(Paths.Endpoints.TEST, response.getBody().getPath());
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleDataIntegrityViolation_ReturnsConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(ErrorCode.DATABASE_ERROR.getMessage());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(ex, webRequest);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
        assertEquals(ErrorCode.DATABASE_ERROR, response.getBody().getErrorCode());
        assertEquals(ErrorCode.DATABASE_ERROR.getMessage(), response.getBody().getMessage());
        assertEquals(Paths.Endpoints.TEST, response.getBody().getPath());
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleBudgetNotFound_ReturnsNotFound() {
        String errorMessage = ErrorCode.RESOURCE_NOT_FOUND.getMessage();
        BudgetNotFoundException ex = new BudgetNotFoundException(errorMessage);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBudgetNotFound(ex, webRequest);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, response.getBody().getErrorCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(Paths.Endpoints.TEST, response.getBody().getPath());
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleIncomeNotFound_ReturnsNotFound() {
        String errorMessage = ErrorCode.RESOURCE_NOT_FOUND.getMessage();
        IncomeNotFoundException ex = new IncomeNotFoundException(errorMessage);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIncomeNotFound(ex, webRequest);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, response.getBody().getErrorCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(Paths.Endpoints.TEST, response.getBody().getPath());
        assertTrue(response.getBody().getErrors().isEmpty());
    }
    
    @Test
    void handleExpenseNotFound_ReturnsNotFound() {
        String errorMessage = ErrorCode.RESOURCE_NOT_FOUND.getMessage();
        ExpenseNotFoundException ex = new ExpenseNotFoundException(errorMessage);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleExpenseNotFound(ex, webRequest);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, response.getBody().getErrorCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(Paths.Endpoints.TEST, response.getBody().getPath());
        assertTrue(response.getBody().getErrors().isEmpty());
    }
} 