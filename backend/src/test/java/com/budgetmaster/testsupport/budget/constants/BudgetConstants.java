package com.budgetmaster.testsupport.budget.constants;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

public final class BudgetConstants {
    private BudgetConstants() {}

    public static final class Default {
        private Default() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("1000.00");
        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("500.00");
        public static final BigDecimal SAVINGS = new BigDecimal("500.00");
        public static final Currency CURRENCY = Currency.getInstance("GBP");
        public static final int YEAR = 2000;
        public static final int MONTH = 1;
        public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
        public static final String YEAR_MONTH_STRING = YEAR_MONTH.toString();
        public static final Long ID = 1L;
    }

    public static final class Updated {
        private Updated() {}

        public static final int YEAR = 1999;
        public static final int MONTH = 12;
        public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
    }

    public static final class NonExistent {
        private NonExistent() {}

        public static final Long ID = 999L;
        public static final int YEAR = 3000;
        public static final int MONTH = 1;
        public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
        public static final String YEAR_MONTH_STRING = YEAR_MONTH.toString();
    }

    public static final class ZeroValues {
        private ZeroValues() {}

        public static final BigDecimal TOTAL_INCOME = BigDecimal.ZERO;
        public static final BigDecimal TOTAL_EXPENSE = BigDecimal.ZERO;
        public static final BigDecimal SAVINGS = BigDecimal.ZERO;
    }

    // -- Arithmetic Constants --

    public static final class AfterAddIncome_WhenBudgetExists {
        private AfterAddIncome_WhenBudgetExists() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("2000.00");
        public static final BigDecimal SAVINGS = new BigDecimal("1500.00");
    }

    public static final class AfterSubtractIncome_WhenBudgetExists {
        private AfterSubtractIncome_WhenBudgetExists() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("0.00");
        public static final BigDecimal SAVINGS = new BigDecimal("-500.00");
    }

    public static final class AfterAddExpense_WhenBudgetExists {
        private AfterAddExpense_WhenBudgetExists() {}

        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("1000.00");
        public static final BigDecimal SAVINGS = new BigDecimal("0.00");
    }

    public static final class AfterSubtractExpense_WhenBudgetExists {
        private AfterSubtractExpense_WhenBudgetExists() {}

        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("0.00");
        public static final BigDecimal SAVINGS = new BigDecimal("1000.00");
    }

    public static final class AfterAddIncome_WhenNoBudgetExists {
        private AfterAddIncome_WhenNoBudgetExists() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("1000.00");
        public static final BigDecimal SAVINGS = new BigDecimal("500.00");
    }

    public static final class AfterAddExpense_WhenNoBudgetExists {
        private AfterAddExpense_WhenNoBudgetExists() {}

        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("500.00");
        public static final BigDecimal SAVINGS = new BigDecimal("-500.00");
    }

    // -- Synchronization Constants --

    public static final class AfterReapplyExpense_SameMonth {
        private AfterReapplyExpense_SameMonth() {}

        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("750.00");
        public static final BigDecimal SAVINGS = new BigDecimal("250.00");
    }

    public static final class AfterReapplyExpense_DifferentMonth {
        private AfterReapplyExpense_DifferentMonth() {}

        public static final class NewBudget {
            private NewBudget() {}

            public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("750.00");
            public static final BigDecimal SAVINGS = new BigDecimal("-750.00");
        }

        public static final class ExistingBudget {
            private ExistingBudget() {}

            public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("0.00");
            public static final BigDecimal SAVINGS = new BigDecimal("1000.00");
        }
    }

    public static final class AfterReapplyIncome_SameMonth {
        private AfterReapplyIncome_SameMonth() {}

        public static final BigDecimal TOTAL_INCOME = new BigDecimal("750.00");
        public static final BigDecimal SAVINGS = new BigDecimal("250.00");
    }

    public static final class AfterReapplyIncome_DifferentMonth {
        private AfterReapplyIncome_DifferentMonth() {}

        public static final class NewBudget {
            private NewBudget() {}

            public static final BigDecimal TOTAL_INCOME = new BigDecimal("750.00");
            public static final BigDecimal SAVINGS = new BigDecimal("750.00");
        }

        public static final class ExistingBudget {
            private ExistingBudget() {}

            public static final BigDecimal TOTAL_INCOME = new BigDecimal("0.00");
            public static final BigDecimal SAVINGS = new BigDecimal("-500.00");
        }
        
    }

    public static final class AfterRetractExpense {
        private AfterRetractExpense() {}

        public static final BigDecimal TOTAL_EXPENSE = new BigDecimal("0.00");
        public static final BigDecimal SAVINGS = new BigDecimal("1000.00");
    }

    public static final class AfterRetractIncome {
        private AfterRetractIncome() {}
        
        public static final BigDecimal TOTAL_INCOME = new BigDecimal("0.00");
        public static final BigDecimal SAVINGS = new BigDecimal("-500.00");
    }
}
