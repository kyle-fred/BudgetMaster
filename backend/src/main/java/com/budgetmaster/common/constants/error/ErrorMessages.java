package com.budgetmaster.constants.error;

public class ErrorMessages {

    public static class Common {
        public static final String ERROR = "error";
        public static final String INVALID_REQUEST = "Invalid request format.";
        public static final String UNEXPECTED_ERROR = "An unexpected error occurred.";
        public static final String DATABASE_CONSTRAINT_VIOLATION = "A database constraint was violated.";
    }

    public static class Budget {
        public static final String NOT_FOUND_BY_ID = "Budget not found with id: %s";
        public static final String NOT_FOUND_BY_MONTH = "Budget not found for month: %s";
    }

    public static class Enum {
        public static final String INVALID_VALUE = "Invalid enum value.";
        public static final String INVALID_VALUE_FORMAT = "Invalid value '%s' for '%s'. Allowed values: [%s]";
    }

    public static class Expense {
        public static final String NOT_FOUND_BY_ID = "Expense not found with id: %s";
        public static final String NOT_FOUND_BY_MONTH = "Expense not found for month: %s";
    }

    public static class Income {
        public static final String NOT_FOUND_BY_ID = "Income not found with id: %s";
        public static final String NOT_FOUND_BY_MONTH = "Income not found for month: %s";
    }
    
    public static class Currency {
        public static final String UNSUPPORTED = "Currency %s is not supported";
        public static final String UNSUPPORTED_FOR_CONSTRAINT_ANNOTATION = "This currency type is not supported yet";
        public static final String MISMATCH = "Currency mismatch: %s vs %s";
    }
}

