package com.budgetmaster.application.exception;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.exception.dto.ErrorResponse;
import com.budgetmaster.testsupport.assertions.dto.ErrorResponseAssertions;
import com.budgetmaster.testsupport.constants.ExceptionConstants;
import com.budgetmaster.testsupport.constants.PathConstants;
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
        when(webRequest.getDescription(false)).thenReturn(PathConstants.Endpoints.TEST);
    }
    
    @Test
    void handleValidationExceptions_WithFieldErrors_ReturnsBadRequestWithValidationErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError(
            ExceptionConstants.Validation.OBJECT_NAME, 
            ExceptionConstants.Validation.FIELD_NAME, 
            ExceptionConstants.Validation.ERROR_MESSAGE
        );
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(ex, webRequest);
        
        ErrorResponseAssertions.assertErrorResponse(response)
            .matchesValidationError(ExceptionConstants.Validation.FIELD_NAME, ExceptionConstants.Validation.ERROR_MESSAGE);
    }
    
    @Test
    void handleConstraintViolation_WithViolations_ReturnsBadRequestWithValidationErrors() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        
        when(ex.getConstraintViolations()).thenReturn(Set.of(violation));
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn(ExceptionConstants.Validation.FIELD_PATH);
        when(violation.getMessage()).thenReturn(ExceptionConstants.Validation.ERROR_MESSAGE);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolation(ex, webRequest);
        
        ErrorResponseAssertions.assertErrorResponse(response)
            .matchesValidationError(ExceptionConstants.Validation.FIELD_NAME, ExceptionConstants.Validation.ERROR_MESSAGE);
    }
    
    @Test
    void handleInvalidRequest_WithValueInstantiationException_ReturnsBadRequest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        ValueInstantiationException valueEx = mock(ValueInstantiationException.class);
        JsonMappingException.Reference reference = mock(JsonMappingException.Reference.class);
        Object from = new Object();
        
        when(ex.getCause()).thenReturn(valueEx);
        when(valueEx.getPath()).thenReturn(List.of(reference));
        when(reference.getFieldName()).thenReturn(ExceptionConstants.Validation.FIELD_NAME);
        when(reference.getFrom()).thenReturn(from);
        when(valueEx.getMessage()).thenReturn(ExceptionConstants.Enum.INVALID_VALUE_MESSAGE);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidRequest(ex, webRequest);

        ErrorResponseAssertions.assertErrorResponse(response)
            .matchesInvalidEnumValue();
    }
    
    @Test
    void handleGenericException_ReturnsInternalServerError() {
        Exception ex = new Exception(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex, webRequest);
        
        ErrorResponseAssertions.assertErrorResponse(response)
            .matchesInternalServerError();
    }
    
    @Test
    void handleDataIntegrityViolation_ReturnsConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(ErrorCode.DATABASE_ERROR.getMessage());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(ex, webRequest);
        
        ErrorResponseAssertions.assertErrorResponse(response)
            .matchesConflict();
    }
    
    @Test
    void handleBudgetNotFound_ReturnsNotFound() {
        BudgetNotFoundException ex = new BudgetNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBudgetNotFound(ex, webRequest);

        ErrorResponseAssertions.assertErrorResponse(response)
            .matchesResourceNotFound();
    }
    
    @Test
    void handleIncomeNotFound_ReturnsNotFound() {
        IncomeNotFoundException ex = new IncomeNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIncomeNotFound(ex, webRequest);
        
        ErrorResponseAssertions.assertErrorResponse(response)
            .matchesResourceNotFound();
    }
    
    @Test
    void handleExpenseNotFound_ReturnsNotFound() {
        ExpenseNotFoundException ex = new ExpenseNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleExpenseNotFound(ex, webRequest);
        
        ErrorResponseAssertions.assertErrorResponse(response)
            .matchesResourceNotFound();
    }
} 