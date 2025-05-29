package com.budgetmaster.testsupport.constants;

public class Paths {
    
    public static class Endpoints {
        private Endpoints() {}

        public static final String BUDGET = "/api/budgets";
        public static final String BUDGET_WITH_ID = BUDGET + "/{id}";

        public static final String EXPENSE = "/api/expenses";
        public static final String EXPENSE_WITH_ID = EXPENSE + "/{id}";

        public static final String INCOME = "/api/incomes";
        public static final String INCOME_WITH_ID = INCOME + "/{id}";
    }

    public static class JsonProperties {
        private JsonProperties() {}

        public static class Budget {
            private Budget() {}

            public static final String ID = "$.id";
            public static final String TOTAL_INCOME = "$.total-income";
            public static final String TOTAL_EXPENSE = "$.total-expense";
            public static final String SAVINGS = "$.savings";
            public static final String CURRENCY = "$.currency";
            public static final String MONTH_YEAR = "$.month";
            public static final String YEAR = "$.month[0]";
            public static final String MONTH = "$.month[1]";
        }

        public static class Expense {
            private Expense() {}

            public static final String ID = "$.id";
            public static final String NAME = "$.name";
            public static final String MONEY = "$.money";
            public static final String MONEY_AMOUNT = "$.money.amount";
            public static final String MONEY_CURRENCY = "$.money.currency";
            public static final String CATEGORY = "$.category";
            public static final String TYPE = "$.type";
            public static final String MONTH_YEAR = "$.month";
            public static final String YEAR = "$.month[0]";
            public static final String MONTH = "$.month[1]";

            public static final String FIRST_NAME = "$[0].name";
            public static final String SECOND_NAME = "$[1].name";
        }

        public static class Income {
            private Income() {}

            public static final String ID = "$.id";
            public static final String NAME = "$.name";
            public static final String SOURCE = "$.source";
            public static final String MONEY = "$.money";
            public static final String MONEY_AMOUNT = "$.money.amount";
            public static final String MONEY_CURRENCY = "$.money.currency";
            public static final String TYPE = "$.type";
            public static final String MONTH_YEAR = "$.month";
            public static final String YEAR = "$.month[0]";
            public static final String MONTH = "$.month[1]";

            public static final String FIRST_NAME = "$[0].name";
            public static final String SECOND_NAME = "$[1].name";
        }

        public static class Error {
            private Error() {}

            // public static final String ERROR = "$.error";
        }
    }

    public static class RequestParams {
        private RequestParams() {}
        
        public static final String MONTH = "month";
    }
    
}