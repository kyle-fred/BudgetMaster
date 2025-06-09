package com.budgetmaster.testsupport.constants;

public final class ExceptionConstants {
  private ExceptionConstants() {}

  public static class Validation {
    private Validation() {}

    public static final String OBJECT_NAME = "object";
    public static final String FIELD_NAME = "field";
    public static final String ERROR_MESSAGE = "error message";
    public static final String FIELD_PATH = "object.field";
  }

  public static class Enum {
    private Enum() {}

    public static final String INVALID_VALUE_MESSAGE = "Invalid enum value: INVALID";
  }
}
