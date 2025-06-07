package com.budgetmaster.testsupport.constants;

public final class ErrorConstants {
    private ErrorConstants() {}

    public static class Budget {
        private Budget() {}
        
        public static final String NOT_FOUND_FOR_MONTH = "Budget not found for month: %s";
        public static final String NOT_FOUND_WITH_ID = "Budget not found with id: %s";
    }

    public static class Expense {
        private Expense() {}
        
        public static final String NOT_FOUND_FOR_MONTH = "No expenses found for month: %s";
        public static final String NOT_FOUND_WITH_ID = "Expense not found with id: %s";
        public static final String NAME_IS_REQUIRED = "Expense name is required.";
        public static final String CATEGORY_IS_REQUIRED = "Expense category is required.";
        public static final String TYPE_IS_REQUIRED = "The transaction type is required.";
    }

    public static class Income {
        private Income() {}
        
        public static final String NOT_FOUND_FOR_MONTH = "No incomes found for month: %s";
        public static final String NOT_FOUND_WITH_ID = "Income not found with id: %s";
        public static final String NAME_IS_REQUIRED = "Income name is required.";
        public static final String SOURCE_IS_REQUIRED = "Income source is required.";
        public static final String TYPE_IS_REQUIRED = "The transaction type is required.";
    }

    public static class Money {
        private Money() {}
        
        public static final String AMOUNT_IS_REQUIRED = "Amount is required.";
        public static final String CURRENCY_IS_REQUIRED = "Currency is required.";
        public static final String CURRENCY_IS_NOT_SUPPORTED = "This currency type is not supported yet";
        public static final String AMOUNT_MUST_BE_NON_NEGATIVE = "Amount must be non-negative.";
        public static final String DETAILS_ARE_REQUIRED = "Money details (amount and currency) are required.";
    }

    public static class Month {
        private Month() {}
        
        public static final String IS_REQUIRED = "Month is required.";
        public static final String INVALID_FORMAT = "Invalid month format. Please use the format YYYY-MM.";
    }

    public static class Enum {
        private Enum() {}
        
        // -- Example Exception Messages --
        public static final String EXAMPLE_ENUM_EXCEPTION = "java.lang.IllegalArgumentException: No enum constant com.example.Color.BLUEE";
        public static final String EXAMPLE_NON_ENUM_EXCEPTION = "java.lang.IllegalArgumentException: Some other error";

        // -- Enum Extraction Parts --
        public static final String EXTRACTED_ENUM_PREFIX = "No enum constant com.example.Color.BLUEE";
        public static final String EXTRACTED_ENUM_CLASS = "com.example.Color.BLUEE";
        public static final String EXTRACTED_ENUM_VALUE = "BLUEE";
    }
}