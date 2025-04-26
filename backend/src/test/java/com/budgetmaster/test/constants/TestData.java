package com.budgetmaster.test.constants;

import java.math.BigDecimal;
import java.time.YearMonth;

public class TestData {
    public static class SharedTestData {
        // -- IDs --    
        public static final Long TEST_EXISTING_ID = 1L;
        public static final Long TEST_NON_EXISTING_ID = 99L;

        // -- Months --
        public static final YearMonth TEST_EXISTING_MONTH_YEAR = YearMonth.of(2000, 1);
        public static final YearMonth TEST_NON_EXISTING_MONTH_YEAR = YearMonth.of(2000, 2);
        public static final String TEST_EXISTING_MONTH_YEAR_STRING = "2000-01";
        public static final String TEST_NON_EXISTING_MONTH_STRING = "2000-02";
        public static final String TEST_INVALID_MONTH = "2000-13";
        public static final String TEST_INVALID_MONTH_FORMAT = "2000/01";
    }
    
    public static class BudgetTestData {
        public static final BigDecimal TEST_INCOME = new BigDecimal("543.21");
        public static final BigDecimal TEST_EXPENSE = new BigDecimal("123.45");
    }

    public static class ExpenseTestData {
        public static final String TEST_NAME = "Test Expense";
        public static final BigDecimal TEST_AMOUNT = new BigDecimal("123.45");
    }

    public static class IncomeTestData {
        public static final String TEST_NAME = "Test Income";
        public static final String TEST_SOURCE = "Test Source";
        public static final BigDecimal TEST_AMOUNT = new BigDecimal("123.45");
    }
}