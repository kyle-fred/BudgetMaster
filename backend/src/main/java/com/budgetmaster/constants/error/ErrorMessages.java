package com.budgetmaster.constants.error;

public class ErrorMessages {
    public class CommonErrorMessages {
        public static final String ERROR_MESSAGE_ERROR = "error";
        public static final String ERROR_MESSAGE_INVALID_REQUEST = "Invalid request format.";
        public static final String ERROR_MESSAGE_UNEXPECTED_ERROR = "An unexpected error occurred.";
        public static final String ERROR_MESSAGE_DATABASE_CONSTRAINT_VIOLATION = "A database constraint was violated.";
    }
    
    public class CurrencyErrorMessages {
        public static final String ERROR_MESSAGE_UNSUPPORTED_CURRENCY = "Currency %s is not supported";
    }
}
