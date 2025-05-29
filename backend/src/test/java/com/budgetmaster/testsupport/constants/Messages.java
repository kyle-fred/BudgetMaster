package com.budgetmaster.testsupport.constants;

public class Messages {

    public static class Error {
        private Error() {}
        
        public static final String DUPLICATE_ENTRY = "Duplicate entry";
        public static final String SYNCHRONIZATION_FAILED = "Synchronization failed";
    }

    public static class Budget {
        private Budget() {}
        
        public static final String NOT_FOUND_FOR_MONTH = "Budget not found for month: %s";
        public static final String NOT_FOUND_WITH_ID = "Budget not found with id: %s";
    }

    public static class Expense {
        private Expense() {}
        
        public static final String NOT_FOUND_WITH_ID = "Expense not found with id: %s";
        public static final String NOT_FOUND_BY_MONTH = "No expenses found for month: %s";
        public static final String NAME_REQUIRED = "Expense name is required.";
        public static final String CATEGORY_REQUIRED = "Expense category is required.";
        public static final String TYPE_REQUIRED = "The transaction type is required.";
    }

    public static class Income {
        private Income() {}
        
        public static final String NOT_FOUND_WITH_ID = "Income not found with id: %s";
        public static final String NOT_FOUND_BY_MONTH = "No incomes found for month: %s";
        public static final String NAME_REQUIRED = "Income name is required.";
        public static final String SOURCE_REQUIRED = "Income source is required.";
        public static final String TYPE_REQUIRED = "The transaction type is required.";
    }

    public static class Money {
        private Money() {}
        
        public static final String AMOUNT_REQUIRED = "Amount is required.";
        public static final String CURRENCY_REQUIRED = "Currency is required.";
        public static final String UNSUPPORTED_CURRENCY = "This currency type is not supported yet";
        public static final String NEGATIVE_AMOUNT = "Amount must be non-negative.";
        public static final String DETAILS_REQUIRED = "Money details (amount and currency) are required.";
    }

    public static class Month {
        private Month() {}
        
        public static final String REQUIRED = "Month is required.";
        public static final String INVALID_FORMAT = "Invalid month format. Please use the format YYYY-MM.";
    }

    public static class Enum {
        private Enum() {}
        
        // -- Test Exception Messages --
        public static final String FULL_EXCEPTION_INVALID_ENUM = "java.lang.IllegalArgumentException: No enum constant com.example.Color.BLUEE";
        public static final String FULL_EXCEPTION_OTHER = "java.lang.IllegalArgumentException: Some other error";

        // -- Enum Extraction Parts --
        public static final String NO_ENUM_PREFIX = "No enum constant com.example.Color.BLUEE";
        public static final String ENUM_PART = "com.example.Color.BLUEE";
        public static final String ENUM_CONSTANT = "BLUEE";
    }

}