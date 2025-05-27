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
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.common.constants.error.ErrorMessages;
import com.budgetmaster.common.constants.string.StringConstants;
import com.budgetmaster.common.dto.ErrorResponse;
import com.budgetmaster.common.dto.builder.ErrorResponseBuilder;
import com.budgetmaster.common.enums.ErrorCode;
import com.budgetmaster.common.utils.EnumExceptionUtils;
import com.budgetmaster.expense.exception.ExpenseNotFoundException;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.Optional;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(
			MethodArgumentNotValidException ex, 
			WebRequest request) {
		ErrorResponseBuilder builder = new ErrorResponseBuilder()
			.status(HttpStatus.BAD_REQUEST.value())
			.errorCode(ErrorCode.VALIDATION_ERROR)
			.message(ErrorCode.VALIDATION_ERROR.getMessage())
			.path(request.getDescription(false));

		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			builder.addError(fieldName, errorMessage);
		});

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(builder.build());
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(
			ConstraintViolationException ex, 
			WebRequest request) {
		ErrorResponseBuilder builder = new ErrorResponseBuilder()
			.status(HttpStatus.BAD_REQUEST.value())
			.errorCode(ErrorCode.VALIDATION_ERROR)
			.message(ErrorCode.VALIDATION_ERROR.getMessage())
			.path(request.getDescription(false));

		ex.getConstraintViolations().forEach(violation -> {
			String fieldName = violation.getPropertyPath().toString();
			fieldName = fieldName.substring(fieldName.lastIndexOf(StringConstants.Punctuation.DOT) + 1);
			String errorMessage = violation.getMessage();
			builder.addError(fieldName, errorMessage);
		});

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(builder.build());
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleInvalidRequest(
			HttpMessageNotReadableException ex, 
			WebRequest request) {
		Throwable cause = ex.getCause();
		
		if (cause instanceof ValueInstantiationException valueEx) {
			return handleInvalidEnumValue(valueEx, request);
		}
		
		ErrorResponse response = new ErrorResponseBuilder()
			.status(HttpStatus.BAD_REQUEST.value())
			.errorCode(ErrorCode.INVALID_INPUT)
			.message(ErrorCode.INVALID_INPUT.getMessage())
			.path(request.getDescription(false))
			.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleGenericException(
			Exception ex, 
			WebRequest request) {
		ErrorResponse response = new ErrorResponseBuilder()
			.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
			.message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
			.path(request.getDescription(false))
			.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
			DataIntegrityViolationException ex, 
			WebRequest request) {
		ErrorResponse response = new ErrorResponseBuilder()
			.status(HttpStatus.CONFLICT.value())
			.errorCode(ErrorCode.DATABASE_ERROR)
			.message(ErrorCode.DATABASE_ERROR.getMessage())
			.path(request.getDescription(false))
			.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	@ExceptionHandler(BudgetNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleBudgetNotFound(
			BudgetNotFoundException ex, 
			WebRequest request) {
		ErrorResponse response = new ErrorResponseBuilder()
			.status(HttpStatus.NOT_FOUND.value())
			.errorCode(ErrorCode.RESOURCE_NOT_FOUND)
			.message(ex.getMessage())
			.path(request.getDescription(false))
			.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@ExceptionHandler(IncomeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleIncomeNotFound(
			IncomeNotFoundException ex, 
			WebRequest request) {
		ErrorResponse response = new ErrorResponseBuilder()
			.status(HttpStatus.NOT_FOUND.value())
			.errorCode(ErrorCode.RESOURCE_NOT_FOUND)
			.message(ex.getMessage())
			.path(request.getDescription(false))
			.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@ExceptionHandler(ExpenseNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleExpenseNotFound(
			ExpenseNotFoundException ex, 
			WebRequest request) {
		ErrorResponse response = new ErrorResponseBuilder()
			.status(HttpStatus.NOT_FOUND.value())
			.errorCode(ErrorCode.RESOURCE_NOT_FOUND)
			.message(ex.getMessage())
			.path(request.getDescription(false))
			.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	private ResponseEntity<ErrorResponse> handleInvalidEnumValue(
			ValueInstantiationException ex, 
			WebRequest request) {
		JsonMappingException.Reference reference = ex.getPath().get(0);
		String fieldName = reference.getFieldName();
		String invalidValue = EnumExceptionUtils.extractInvalidEnumValue(ex.getMessage());

		Class<?> fieldType = reference.getFrom().getClass();
		Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(fieldType, fieldName);

		if (enumType.isPresent()) {
			String errorMessage = String.format(
				ErrorMessages.Enum.INVALID_VALUE_FORMAT,
				invalidValue, 
				fieldName, 
				EnumExceptionUtils.getEnumValuesAsString(enumType.get())
			);

			ErrorResponse response = new ErrorResponseBuilder()
				.status(HttpStatus.BAD_REQUEST.value())
				.errorCode(ErrorCode.INVALID_ENUM_VALUE)
				.message(errorMessage)
				.path(request.getDescription(false))
				.build();

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		ErrorResponse response = new ErrorResponseBuilder()
			.status(HttpStatus.BAD_REQUEST.value())
			.errorCode(ErrorCode.INVALID_ENUM_VALUE)
			.message(ErrorCode.INVALID_ENUM_VALUE.getMessage())
			.path(request.getDescription(false))
			.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}