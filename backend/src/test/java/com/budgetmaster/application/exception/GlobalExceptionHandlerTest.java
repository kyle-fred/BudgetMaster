package com.budgetmaster.application.exception;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;
    
    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn(PathConstants.Endpoints.TEST);
    }

    @Nested
    @DisplayName("Validation Exception Handling")
    class ValidationExceptionHandling {
        
        @Test
        @DisplayName("Should handle method argument validation with field errors")
        void handleValidationExceptions_withFieldErrors_returnsBadRequestWithValidationErrors() {
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
        @DisplayName("Should handle constraint violations")
        void handleConstraintViolation_withViolations_returnsBadRequestWithValidationErrors() {
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
    }

    @Nested
    @DisplayName("Request Processing Exception Handling")
    class RequestProcessingExceptionHandling {
        
        @Test
        @DisplayName("Should handle invalid enum value in request")
        void handleInvalidRequest_withValueInstantiationException_returnsBadRequest() {
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
    }

    @Nested
    @DisplayName("System Exception Handling")
    class SystemExceptionHandling {
        
        @Test
        @DisplayName("Should handle generic exceptions with internal server error")
        void handleGenericException_returnsInternalServerError() {
            Exception ex = new Exception(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
            
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex, webRequest);
            
            ErrorResponseAssertions.assertErrorResponse(response)
                .matchesInternalServerError();
        }
        
        @Test
        @DisplayName("Should handle database integrity violations with conflict")
        void handleDataIntegrityViolation_returnsConflict() {
            DataIntegrityViolationException ex = new DataIntegrityViolationException(ErrorCode.DATABASE_ERROR.getMessage());
            
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(ex, webRequest);
            
            ErrorResponseAssertions.assertErrorResponse(response)
                .matchesConflict();
        }
    }

    @Nested
    @DisplayName("Resource Not Found Handling")
    class ResourceNotFoundHandling {
        
        @Test
        @DisplayName("Should handle budget not found")
        void handleBudgetNotFound_returnsNotFound() {
            BudgetNotFoundException ex = new BudgetNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
            
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBudgetNotFound(ex, webRequest);

            ErrorResponseAssertions.assertErrorResponse(response)
                .matchesResourceNotFound();
        }
        
        @Test
        @DisplayName("Should handle income not found")
        void handleIncomeNotFound_returnsNotFound() {
            IncomeNotFoundException ex = new IncomeNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
            
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIncomeNotFound(ex, webRequest);
            
            ErrorResponseAssertions.assertErrorResponse(response)
                .matchesResourceNotFound();
        }
        
        @Test
        @DisplayName("Should handle expense not found")
        void handleExpenseNotFound_returnsNotFound() {
            ExpenseNotFoundException ex = new ExpenseNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
            
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleExpenseNotFound(ex, webRequest);
            
            ErrorResponseAssertions.assertErrorResponse(response)
                .matchesResourceNotFound();
        }
    }
} 