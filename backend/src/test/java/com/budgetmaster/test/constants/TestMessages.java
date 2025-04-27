package com.budgetmaster.test.constants;

public class TestMessages {
    public static class CommonErrorMessages {
        public static final String TEST_MESSAGE_SERVICE_FAILURE = "Service failure";
        public static final String TEST_MESSAGE_INTERNAL_SERVER_ERROR = "An unexpected error occurred.";
        public static final String TEST_MESSAGE_DATABASE_CONSTRAINT_VIOLATION = "A database constraint was violated.";
    }

    public static class BudgetErrorMessages {
        public static final String TEST_MESSAGE_BUDGET_NOT_FOUND_FOR_MONTH = "Budget not found for month: %s";
        public static final String TEST_MESSAGE_BUDGET_NOT_FOUND_WITH_ID = "Budget not found with id: %s";
    }

    public static class ExpenseErrorMessages {
        public static final String TEST_MESSAGE_EXPENSE_NOT_FOUND_WITH_ID = "Expense not found with id: %s";
    }

    public static class IncomeErrorMessages {
        public static final String TEST_MESSAGE_INCOME_NOT_FOUND_WITH_ID = "Income not found with id: %s";
    }

    public static class MoneyErrorMessages {
        public static final String TEST_MESSAGE_MONEY_AMOUNT_REQUIRED = "Amount is required.";
        public static final String TEST_MESSAGE_MONEY_CURRENCY_REQUIRED = "Currency is required.";
        public static final String TEST_MESSAGE_MONEY_UNSUPPORTED_CURRENCY = "Unsupported currency.";
        public static final String TEST_MESSAGE_MONEY_NEGATIVE_AMOUNT = "Amount must be non-negative.";
        
    }
}