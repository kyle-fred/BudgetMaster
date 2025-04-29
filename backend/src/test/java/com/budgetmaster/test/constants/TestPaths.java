package com.budgetmaster.test.constants;

public class TestPaths {
    
    public static class EndpointPathConstants {
        // -- Budget --
        public static final String ENDPOINT_BUDGET = "/api/budgets";
        public static final String ENDPOINT_BUDGET_WITH_ID = ENDPOINT_BUDGET + "/{id}";

        // -- Expense --
        public static final String ENDPOINT_EXPENSE = "/api/expenses";
        public static final String ENDPOINT_EXPENSE_WITH_ID = ENDPOINT_EXPENSE + "/{id}";

        // -- Income --
        public static final String ENDPOINT_INCOME = "/api/incomes";
        public static final String ENDPOINT_INCOME_WITH_ID = ENDPOINT_INCOME + "/{id}";
    }

    public static class JsonPathConstants {
        // -- Common --
        public static final String JSON_PATH_ID = "$.id";
        public static final String JSON_PATH_ERROR = "$.error";
        public static final String JSON_PATH_MONTH = "$.month";
        public static final String JSON_PATH_CURRENCY = "$.currency";
        public static final String JSON_PATH_EMPTY = "$";

        // -- Budget --
        public static final String JSON_PATH_TOTAL_INCOME = "$.total-income";
        public static final String JSON_PATH_TOTAL_EXPENSE = "$.total-expense";
        public static final String JSON_PATH_SAVINGS = "$.savings";

        // -- Expense & Income --
        public static final String JSON_PATH_NAME = "$.name";
        public static final String JSON_PATH_NAME_0 = "$[0].name";
        public static final String JSON_PATH_NAME_1 = "$[1].name";
        public static final String JSON_PATH_MONEY_AMOUNT = "$.money.amount";
        public static final String JSON_PATH_MONEY_CURRENCY = "$.money.currency";
        public static final String JSON_PATH_TYPE = "$.type";

        // -- Expense --
        public static final String JSON_PATH_CATEGORY = "$.category";

        // -- Income --
        public static final String JSON_PATH_SOURCE = "$.source";
    }

    public static class RequestParamsConstants {
        public static final String REQUEST_PARAM_MONTH = "month";
    }
}