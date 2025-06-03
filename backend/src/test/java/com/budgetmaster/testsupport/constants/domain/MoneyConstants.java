package com.budgetmaster.testsupport.constants.domain;

import java.math.BigDecimal;
import java.util.Currency;

public final class MoneyConstants {
    private MoneyConstants() {}

    public static final Currency GBP = Currency.getInstance("GBP");
    public static final int SCALE = 2;

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

    public static final class CreationInputs {
        private CreationInputs() {}

        public static final BigDecimal BIGDECIMAL_THREE_DP = new BigDecimal("123.456");
        public static final BigDecimal BIGDECIMAL_TWO_DP = new BigDecimal("123.46");

        public static final String STRING_THREE_DP = "123.456";
        public static final double DOUBLE_THREE_DP = 123.456;
    }

    public static final class ArithmeticInputs {
        private ArithmeticInputs() {}

        public static final BigDecimal HUNDRED = new BigDecimal("100.00");
        public static final BigDecimal FIFTY = new BigDecimal("50.00");
        public static final BigDecimal ONE_POINT_FIVE = new BigDecimal("1.5");
        public static final BigDecimal THREE = new BigDecimal("3");

        public static final BigDecimal RESULT_ADD = new BigDecimal("150.00");
        public static final BigDecimal RESULT_SUBTRACT = new BigDecimal("50.00");
        public static final BigDecimal RESULT_MULTIPLY = new BigDecimal("150.00");
        public static final BigDecimal RESULT_DIVIDE = new BigDecimal("33.33");
    }

    public static final class DisplayStrings {
        private DisplayStrings() {}

        public static final String INPUT_STRING = "100.00";
        public static final String EXPECTED_STRING = "£100.00";
    }

    public static final class Miscellaneous {
        private Miscellaneous() {}

        public static final BigDecimal LARGE_AMOUNT = new BigDecimal("999999999999999.99");
        public static final BigDecimal NINETY_NINE_POINT_NINETY_NINE = new BigDecimal("99.99");
    }
}