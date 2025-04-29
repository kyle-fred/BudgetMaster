package com.budgetmaster.test.constants;

public class TestMessages {
    
    public static class CommonErrorMessageConstants {
        public static final String ERROR = "error";
        public static final String SERVICE_FAILURE = "Service failure";
        public static final String UNEXPECTED_ERROR = "An unexpected error occurred.";
        public static final String DATABASE_CONSTRAINT = "A database constraint was violated.";
        public static final String DUPLICATE_ENTRY = "Duplicate Entry";
    }

    public static class BudgetErrorMessageConstants {
        public static final String BUDGET_NOT_FOUND_FOR_MONTH = "Budget not found for month: %s";
        public static final String BUDGET_NOT_FOUND_WITH_ID = "Budget not found with id: %s";
    }

    public static class ExpenseErrorMessageConstants {
        public static final String EXPENSE_NOT_FOUND_WITH_ID = "Expense not found with id: %s";
        public static final String EXPENSE_NOT_FOUND_BY_MONTH = "No expenses found for month: %s";
        public static final String EXPENSE_NAME_REQUIRED = "Expense name is required.";
        public static final String EXPENSE_CATEGORY_REQUIRED = "Expense category is required.";
        public static final String EXPENSE_TYPE_REQUIRED = "The transaction type is required.";
    }

    public static class IncomeErrorMessageConstants {
        public static final String INCOME_NOT_FOUND_WITH_ID = "Income not found with id: %s";
        public static final String INCOME_NOT_FOUND_BY_MONTH = "No incomes found for month: %s";
        public static final String INCOME_NAME_REQUIRED = "Income name is required.";
        public static final String INCOME_SOURCE_REQUIRED = "Income source is required.";
        public static final String INCOME_TYPE_REQUIRED = "The transaction type is required.";
    }

    public static class MoneyErrorMessageConstants {
        public static final String MONEY_AMOUNT_REQUIRED = "Amount is required.";
        public static final String MONEY_CURRENCY_REQUIRED = "Currency is required.";
        public static final String MONEY_UNSUPPORTED_CURRENCY = "Unsupported currency.";
        public static final String MONEY_NEGATIVE_AMOUNT = "Amount must be non-negative.";
        public static final String MONEY_DETAILS_REQUIRED = "Money details (amount and currency) are required.";
    }

    public static class MonthErrorMessageConstants {
        public static final String MONTH_REQUIRED = "Month is required.";
        public static final String MONTH_INVALID_FORMAT = "Invalid month format. Please use the format YYYY-MM.";
    }

    public static class EnumErrorMessageConstants {
        // -- Test Exception Messages --
        public static final String ERROR_MESSAGE_FULL_EXCEPTION_INVALID_ENUM = "java.lang.IllegalArgumentException: No enum constant com.example.Color.BLUEE";
        public static final String ERROR_MESSAGE_FULL_EXCEPTION_OTHER = "java.lang.IllegalArgumentException: Some other error";
    
        // -- Enum Extraction Parts --
        public static final String ERROR_MESSAGE_NO_ENUM_PREFIX = "No enum constant com.example.Color.BLUEE";
        public static final String ERROR_MESSAGE_ENUM_PART = "com.example.Color.BLUEE";
        public static final String ERROR_MESSAGE_ENUM_CONSTANT = "BLUEE";
        public static final String ERROR_MESSAGE_INVALID_ENUM_TYPE = "com.example.Color";
    
        // -- Expected Error Responses --
        public static final String ERROR_MESSAGE_INVALID_ENUM_VALUE_RESPONSE = "Invalid value 'YELLOW' for 'color'. Allowed values: [RED, GREEN, BLUE]";
    
        // -- Fallback Messages --
        public static final String ERROR_MESSAGE_FALLBACK_MESSAGE = "Invalid enum value.";
    }
    
}