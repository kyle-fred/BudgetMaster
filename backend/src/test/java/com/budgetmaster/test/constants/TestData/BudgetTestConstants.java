package com.budgetmaster.test.constants.TestData;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

public final class BudgetTestConstants {
    private BudgetTestConstants() {}

    public static final class Default {
        private Default() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("1000.00");
        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("500.00");
        public static final BigDecimal SAVINGS = new BigDecimal("500.00");
        public static final Currency CURRENCY = Currency.getInstance("GBP");
        public static final int YEAR = 2000;
        public static final int MONTH = 1;
        public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
        public static final Long ID = 1L;
    }

    public static final class Updated {
        private Updated() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("750.00");
        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("750.00");
        public static final BigDecimal SAVINGS = new BigDecimal("0.00");
        public static final Currency CURRENCY = Currency.getInstance("GBP");
        public static final int YEAR = 1999;
        public static final int MONTH = 12;
        public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
        public static final Long ID = 1L;
    }

    public static final class AfterExpenseApplied {
        private AfterExpenseApplied() {}

        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("1000.00");
        public static final BigDecimal SAVINGS = new BigDecimal("0.00");
    }

    public static final class AfterIncomeApplied {
        private AfterIncomeApplied() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("2000.00");
        public static final BigDecimal SAVINGS = new BigDecimal("1500.00");
    }

    public static final class AfterExpenseUpdatedSameMonth {
        private AfterExpenseUpdatedSameMonth() {}

        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("750.00");
        public static final BigDecimal SAVINGS = new BigDecimal("250.00");
    }

    public static final class AfterIncomeUpdatedSameMonth {
        private AfterIncomeUpdatedSameMonth() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("750.00");
        public static final BigDecimal SAVINGS = new BigDecimal("250.00");
    }

    public static final class AfterExpenseMovedToDifferentMonth {
        private AfterExpenseMovedToDifferentMonth() {}

        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("750.00");
        public static final BigDecimal SAVINGS = new BigDecimal("750.00");
    }

    public static final class AfterIncomeMovedToDifferentMonth {
        private AfterIncomeMovedToDifferentMonth() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("750.00");
        public static final BigDecimal SAVINGS = new BigDecimal("750.00");
    }

    public static final class Invalid {
        private Invalid() {}
        
        public static final BigDecimal TOTAL_INCOME = new BigDecimal("1000.00").negate();
        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("500.00").negate();
        public static final BigDecimal SAVINGS = new BigDecimal("500.00").negate();
        public static final String YEAR_MONTH = "1999-13";
        public static final String YEAR_MONTH_FORMAT = "1999/12";
    }

    public static final class NonExistent {
        private NonExistent() {}

        public static final Long ID = 999L;
        public static final int YEAR = 3000;
        public static final int MONTH = 1;
        public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
    }
}
