package com.budgetmaster.constants.database;

public class ColumnNames {

    public static class Audit {
        private Audit() {}
        
        public static final String ID = "ID";
        public static final String CREATED_AT = "CREATED_AT";
        public static final String LAST_UPDATED_AT = "LAST_UPDATED_AT";
    }

    public static class Budget {
        private Budget() {}
        
        public static final String TOTAL_INCOME = "TOTAL_INCOME";
        public static final String TOTAL_EXPENSE = "TOTAL_EXPENSE";
        public static final String SAVINGS = "SAVINGS";
        public static final String CURRENCY = "CURRENCY";
        public static final String MONTH = "MONTH";
    }

    public static class Income {
        private Income() {}
        
        public static final String NAME = "NAME";
        public static final String SOURCE = "SOURCE";
        public static final String TYPE = "TYPE";
        public static final String MONTH = "MONTH";
    }

    public static class Expense {
        private Expense() {}
        
        public static final String NAME = "NAME";
        public static final String CATEGORY = "CATEGORY";
        public static final String TYPE = "TYPE";
        public static final String MONTH = "MONTH";
    }

    public static class Money {
        private Money() {}
        
        public static final String AMOUNT = "AMOUNT";
        public static final String CURRENCY = "CURRENCY";
    }
    
}
