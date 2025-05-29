package com.budgetmaster.constants.error;

public class ErrorMessages {

    public static class Budget {
        private Budget() {}

        public static final String NOT_FOUND_BY_ID = "Budget not found with id: %s";
        public static final String NOT_FOUND_BY_MONTH = "Budget not found for month: %s";
        public static final String NOT_FOUND_BY_ASSOCIATED_INCOME = "Income's original budget not found for month: %s";
        public static final String NOT_FOUND_BY_ASSOCIATED_EXPENSE = "Expense's original budget not found for month: %s";
    }

    public static class Enum {
        private Enum() {}
        
        public static final String INVALID_VALUE_FORMAT = "Invalid value '%s' for '%s'. Allowed values: [%s]";
    }

    public static class Expense {
        private Expense() {}
        
        public static final String NOT_FOUND_BY_ID = "Expense not found with id: %s";
        public static final String NOT_FOUND_BY_MONTH = "Expense not found for month: %s";
    }

    public static class Income {
        private Income() {}
        
        public static final String NOT_FOUND_BY_ID = "Income not found with id: %s";
        public static final String NOT_FOUND_BY_MONTH = "Income not found for month: %s";
    }
    
    public static class Currency {
        private Currency() {}
        
        public static final String UNSUPPORTED = "Currency %s is not supported";
        public static final String UNSUPPORTED_FOR_CONSTRAINT_ANNOTATION = "This currency type is not supported yet";
        public static final String MISMATCH = "Currency mismatch: %s vs %s";
    }

    public static class Serialization {
        private Serialization() {}
        
        public static final String INVALID_BIG_DECIMAL_VALUE = "Invalid BigDecimal value: '%s'. Must be a valid decimal number.";
    }
    
}