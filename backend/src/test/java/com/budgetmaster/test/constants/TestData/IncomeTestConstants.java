package com.budgetmaster.test.constants.TestData;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.common.enums.TransactionType;

public final class IncomeTestConstants {
    private IncomeTestConstants() {}

    public static final class Default {
        private Default() {}

        public static final String NAME = "TEST INCOME";
        public static final String SOURCE = "TEST SOURCE";
        public static final BigDecimal AMOUNT = new BigDecimal("1000.00");
        public static final Currency CURRENCY = Currency.getInstance("GBP");
        public static final TransactionType TYPE = TransactionType.ONE_TIME;
        public static final int YEAR = 2000;
        public static final int MONTH = 1;
        public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
        public static final Long ID = 1L;
    }

    public static final class Updated {
        private Updated() {}

        public static final String NAME = "UPDATED INCOME";
        public static final String SOURCE = "UPDATED SOURCE";
        public static final BigDecimal AMOUNT = new BigDecimal("750.00");
        public static final TransactionType TYPE = TransactionType.RECURRING;
        public static final int YEAR = 1999;
        public static final int MONTH = 12;
        public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
    }

    public static final class Invalid {
        private Invalid() {}
        
        public static final BigDecimal AMOUNT = new BigDecimal("1000.00").negate();
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