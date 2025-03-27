package com.budgetmaster.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.budgetmaster.utils.exception.EnumExceptionUtils;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		return ResponseEntity.badRequest().body(errors);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, Object>> handleInvalidRequest(HttpMessageNotReadableException ex) {
	    Throwable cause = ex.getCause();
	    
	    if (cause instanceof ValueInstantiationException valueEx) {
	        return handleInvalidEnumValue(valueEx);
	    }
	    
	    Map<String, Object> response = new HashMap<>();
	    response.put("error", "Invalid request format.");
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body("Invalid input " + ex.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
	}
	
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("A database constraint was violated.");
    }
    
    @ExceptionHandler(InvalidMonthYearExceptionHandler.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleInvalidMonthYear(InvalidMonthYearExceptionHandler ex) {
    	Map<String, String> errors = new HashMap<>();
    	errors.put("monthYear", ex.getMessage());
    	return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BudgetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleBudgetNotFound(BudgetNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handles cases where an invalid enum value is provided in a request. 
     * Extracts the field name and invalid value, then returns a response 
     * listing allowed values if the enum type is found, or a fallback error otherwise.
     */
	private ResponseEntity<Map<String, Object>> handleInvalidEnumValue(ValueInstantiationException ex) {
	    JsonMappingException.Reference reference = ex.getPath().get(0);
	    String fieldName = reference.getFieldName();
	    String invalidValue = EnumExceptionUtils.extractInvalidEnumValue(ex.getMessage());

	    Class<?> fieldType = reference.getFrom().getClass();
	    Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(fieldType, fieldName);

	    if (enumType.isPresent()) {
	    	Map<String, Object> errorResponse = EnumExceptionUtils.createErrorResonse(invalidValue, fieldName, enumType.get());

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	    }
	    
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EnumExceptionUtils.createFallbackResponse());
	}
}