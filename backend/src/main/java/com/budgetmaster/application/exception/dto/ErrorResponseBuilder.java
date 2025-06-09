package com.budgetmaster.application.exception.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.budgetmaster.application.exception.codes.ErrorCode;

public class ErrorResponseBuilder {

  public LocalDateTime timestamp;
  public int status;
  public ErrorCode errorCode;
  public String message;
  public String path;
  public List<ValidationError> errors;

  public ErrorResponseBuilder() {
    this.timestamp = LocalDateTime.now();
    this.errors = new ArrayList<>();
  }

  public ErrorResponseBuilder status(int status) {
    this.status = status;
    return this;
  }

  public ErrorResponseBuilder errorCode(ErrorCode errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  public ErrorResponseBuilder message(String message) {
    this.message = message;
    return this;
  }

  public ErrorResponseBuilder path(String path) {
    this.path = path;
    return this;
  }

  public ErrorResponseBuilder addError(String field, String message) {
    this.errors.add(new ValidationError(field, message));
    return this;
  }

  public ErrorResponse build() {
    return ErrorResponse.of(this.status, this.errorCode, this.message, this.path, this.errors);
  }
}
