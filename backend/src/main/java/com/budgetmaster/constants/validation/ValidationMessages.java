package com.budgetmaster.constants.validation;

public class ValidationMessages {

    public static final String MONEY_DETAILS_ARE_REQUIRED = "Money details (amount and currency) are required.";
    public static final String MONTH_IS_REQUIRED = "Month is required.";
    public static final String INVALID_MONTH_FORMAT = "Invalid month format. Please use the format YYYY-MM.";
    public static final String TYPE_IS_REQUIRED = "The transaction type is required.";
    
    public static class Money {
        private Money() {}
        
        public static final String AMOUNT_IS_REQUIRED = "Amount is required.";
        public static final String AMOUNT_MUST_BE_NON_NEGATIVE = "Amount must be non-negative.";
        public static final String CURRENCY_IS_REQUIRED = "Currency is required.";
    }

    public static class Expense {
        private Expense() {}
        
        public static final String NAME_IS_REQUIRED = "Expense name is required.";
        public static final String CATEGORY_IS_REQUIRED = "Expense category is required.";
    }

    public static class Income {
        private Income() {}
        
        public static final String NAME_IS_REQUIRED = "Income name is required.";
        public static final String SOURCE_IS_REQUIRED = "Income source is required.";
    }
}