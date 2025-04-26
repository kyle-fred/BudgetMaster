package com.budgetmaster.constants.validation;

public class ValidationMessages {
    public class MoneyValidation {
        public static final String VALIDATION_MESSAGE_REQUIRED_AMOUNT = "Amount is required.";
        public static final String VALIDATION_MESSAGE_NON_NEGATIVE_AMOUNT = "Amount must be non-negative.";
        public static final String VALIDATION_MESSAGE_REQUIRED_CURRENCY = "Currency is required.";
    }
    
    public class ExpenseValidation {
        public static final String VALIDATION_MESSAGE_REQUIRED_NAME = "Expense name is required.";
        public static final String VALIDATION_MESSAGE_REQUIRED_CATEGORY = "Expense category is required.";
    }

    public class IncomeValidation {
        public static final String VALIDATION_MESSAGE_REQUIRED_NAME = "Income name is required.";
        public static final String VALIDATION_MESSAGE_REQUIRED_SOURCE = "Income source is required.";
    }

    public class CommonValidation {
        public static final String VALIDATION_MESSAGE_REQUIRED_MONEY = "Money details (amount and currency) are required.";
        public static final String VALIDATION_MESSAGE_REQUIRED_MONTH = "Month is required.";
        public static final String VALIDATION_MESSAGE_INVALID_MONTH = "Invalid month format. Please use the format YYYY-MM.";
        public static final String VALIDATION_MESSAGE_REQUIRED_TYPE = "The transaction type is required.";
    }
}
