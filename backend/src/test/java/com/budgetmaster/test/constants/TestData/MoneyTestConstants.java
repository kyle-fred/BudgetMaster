package com.budgetmaster.test.constants.TestData;

import java.math.BigDecimal;
import java.util.Currency;

public class MoneyTestConstants {
    private MoneyTestConstants() {}

    public static final Currency GBP = Currency.getInstance("GBP");

    public static final class IncomeDefaults {
        private IncomeDefaults() {}

        public static final BigDecimal AMOUNT = new BigDecimal("1000.00");
        public static final Currency CURRENCY = GBP;
    }

    public static final class ExpenseDefaults {
        private ExpenseDefaults() {}

        public static final BigDecimal AMOUNT = new BigDecimal("500.00");
        public static final Currency CURRENCY = GBP;
    }

    public static final class Updated {
        private Updated() {}

        public static final BigDecimal AMOUNT = new BigDecimal("750.00");
        public static final Currency CURRENCY = GBP;
    }

    public static final class InvalidValues {
        private InvalidValues() {}

        public static final BigDecimal AMOUNT = new BigDecimal("1000.00").negate();
        public static final Currency EUR = Currency.getInstance("EUR");
    }
}