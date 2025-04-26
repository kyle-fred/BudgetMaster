package com.budgetmaster.test.constants;

public class TestPaths {
    public static class TestJsonPaths {
        // -- Common --
        public static final String TEST_JSON_PATH_ID = "$.id";
        public static final String TEST_JSON_PATH_ERROR = "$.error";
        public static final String TEST_JSON_PATH_MONTH = "$.month";
        public static final String TEST_JSON_PATH_CURRENCY = "$.currency";
        public static final String TEST_JSON_PATH_EMPTY = "$";

        // -- Budget --
        public static final String TEST_JSON_PATH_TOTAL_INCOME = "$.total-income";
        public static final String TEST_JSON_PATH_TOTAL_EXPENSE = "$.total-expense";
        public static final String TEST_JSON_PATH_SAVINGS = "$.savings";

        // -- Expense & Income --
        public static final String TEST_JSON_PATH_NAME = "$.name";
        public static final String TEST_JSON_PATH_NAME_0 = "$[0].name";
        public static final String TEST_JSON_PATH_NAME_1 = "$[1].name";
        public static final String TEST_JSON_PATH_MONEY_AMOUNT = "$.money.amount";
        public static final String TEST_JSON_PATH_MONEY_CURRENCY = "$.money.currency";
        public static final String TEST_JSON_PATH_TYPE = "$.type";

        // -- Expense --
        public static final String TEST_JSON_PATH_CATEGORY = "$.category";

        // -- Income --
        public static final String TEST_JSON_PATH_SOURCE = "$.source";
    }

    public static class TestRequestParams {
        public static final String TEST_PARAM_MONTH = "month";
    }

    public static class TestBudgetPaths {
        public static final String TEST_BUDGET_ENDPOINT = "/api/budgets";
        public static final String TEST_BUDGET_ENDPOINT_WITH_ID = TEST_BUDGET_ENDPOINT + "/{id}";
    }

    public static class TestExpensePaths {
        public static final String TEST_EXPENSE_ENDPOINT = "/api/expenses";
        public static final String TEST_EXPENSE_ENDPOINT_WITH_ID = TEST_EXPENSE_ENDPOINT + "/{id}";
    }

    public static class TestIncomePaths {
        public static final String TEST_INCOME_ENDPOINT = "/api/incomes";
        public static final String TEST_INCOME_ENDPOINT_WITH_ID = TEST_INCOME_ENDPOINT + "/{id}";
    }
}