package com.budgetmaster.application.exception.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.budgetmaster.application.exception.codes.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ErrorResponse {

  private LocalDateTime timestamp;
  private int status;
  private ErrorCode errorCode;
  private String message;
  private String path;
  private List<ValidationError> errors;

  protected ErrorResponse() {}

  public static ErrorResponse of(
      int status, ErrorCode errorCode, String message, String path, List<ValidationError> errors) {
    ErrorResponse response = new ErrorResponse();
    response.timestamp = LocalDateTime.now();
    response.status = status;
    response.errorCode = errorCode;
    response.message = message;
    response.path = path;
    response.errors = errors;
    return response;
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
