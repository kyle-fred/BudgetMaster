package com.budgetmaster.constants.database;

public class ColumnNames {
    public class BudgetColumns {
        public static final String COLUMN_NAME_TOTAL_INCOME = "TOTAL_INCOME";
        public static final String COLUMN_NAME_TOTAL_EXPENSE = "TOTAL_EXPENSE";
        public static final String COLUMN_NAME_SAVINGS = "SAVINGS";
        public static final String COLUMN_NAME_COMMON_CURRENCY = "COMMON_CURRENCY";
    }

    public class IncomeColumns {
        public static final String COLUMN_NAME_SOURCE = "SOURCE";
    }

    public class ExpenseColumns {
        public static final String COLUMN_NAME_CATEGORY = "CATEGORY";
    }

    public class TransactionColumns {
        public static final String COLUMN_NAME_TRANSACTION_NAME = "NAME";
        public static final String COLUMN_NAME_TYPE = "TYPE";
        public static final String COLUMN_NAME_AMOUNT = "AMOUNT";
        public static final String COLUMN_NAME_CURRENCY = "CURRENCY";
    }

    public class CommonColumns {
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_MONTH = "MONTH";
        public static final String COLUMN_NAME_CREATED_AT = "CREATED_AT";
        public static final String COLUMN_NAME_LAST_UPDATED_AT = "LAST_UPDATED_AT";
    }
}
