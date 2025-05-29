package com.budgetmaster.common.constants.validation;

public class ValidationMessages {
    
    public static class Money {
        private Money() {}
        
        public static final String REQUIRED_AMOUNT = "Amount is required.";
        public static final String NON_NEGATIVE_AMOUNT = "Amount must be non-negative.";
        public static final String REQUIRED_CURRENCY = "Currency is required.";
    }

    public static class Expense {
        private Expense() {}
        
        public static final String REQUIRED_NAME = "Expense name is required.";
        public static final String REQUIRED_CATEGORY = "Expense category is required.";
    }

    public static class Income {
        private Income() {}
        
        public static final String REQUIRED_NAME = "Income name is required.";
        public static final String REQUIRED_SOURCE = "Income source is required.";
    }

    public static class Common {
        private Common() {}
        
        public static final String REQUIRED_MONEY = "Money details (amount and currency) are required.";
        public static final String REQUIRED_MONTH = "Month is required.";
        public static final String INVALID_MONTH_FORMAT = "Invalid month format. Please use the format YYYY-MM.";
        public static final String REQUIRED_TYPE = "The transaction type is required.";
    }
    
}

