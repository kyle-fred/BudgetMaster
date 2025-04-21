package com.budgetmaster.constants.error;

public class ErrorMessages {
    public class CommonErrorMessages {
        public static final String ERROR_MESSAGE_ERROR = "error";
        public static final String ERROR_MESSAGE_INVALID_REQUEST = "Invalid request format.";
        public static final String ERROR_MESSAGE_UNEXPECTED_ERROR = "An unexpected error occurred.";
        public static final String ERROR_MESSAGE_DATABASE_CONSTRAINT_VIOLATION = "A database constraint was violated.";
    }

    public class BudgetErrorMessages {
        public static final String ERROR_MESSAGE_BUDGET_NOT_FOUND_BY_ID = "Budget not found with id: %s";
        public static final String ERROR_MESSAGE_BUDGET_NOT_FOUND_BY_MONTH = "Budget not found for month: %s";
    }

    public class EnumErrorMessages {
        public static final String ERROR_MESSAGE_INVALID_ENUM_VALUE = "Invalid enum value.";
        public static final String ERROR_MESSAGE_INVALID_ENUM_VALUE_FORMAT = "Invalid value '%s' for '%s'. Allowed values: [%s]";
    }

    public class ExpenseErrorMessages {
        public static final String ERROR_MESSAGE_EXPENSE_NOT_FOUND_BY_ID = "Expense not found with id: %s";
        public static final String ERROR_MESSAGE_EXPENSE_NOT_FOUND_BY_MONTH = "Expense not found for month: %s";
    }

    public class IncomeErrorMessages {
        public static final String ERROR_MESSAGE_INCOME_NOT_FOUND_BY_ID = "Income not found with id: %s";
        public static final String ERROR_MESSAGE_INCOME_NOT_FOUND_BY_MONTH = "Income not found for month: %s";
    }
    
    public class CurrencyErrorMessages {
        public static final String ERROR_MESSAGE_UNSUPPORTED_CURRENCY = "Currency %s is not supported";
        public static final String ERROR_MESSAGE_UNSUPPORTED_CURRENCY_FOR_CONSTRAINT_ANNOTATION = "This currency type is not supported yet";
    }
}
