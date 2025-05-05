package com.budgetmaster.test.constants;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.expense.enums.ExpenseCategory;
import com.budgetmaster.money.enums.SupportedCurrency;

public class TestData {
    
    public static class CommonTestDataConstants {
        // -- IDs --    
        public static final Long ID_EXISTING = 1L;
        public static final Long ID_NON_EXISTING = 99L;

        // -- Common Strings --
        public static final String EMPTY_STRING = "";
    }

    public static class MonthTestDataConstants {
        public static final YearMonth MONTH_EXISTING = YearMonth.of(2000, 1);
        public static final YearMonth MONTH_NON_EXISTING = YearMonth.of(2000, 2);

        // -- String --
        public static final String MONTH_STRING_EXISTING = "2000-01";
        public static final String MONTH_STRING_NON_EXISTING = "2000-02";
        public static final String MONTH_INVALID = "2000-13";
        public static final String MONTH_INVALID_FORMAT = "2000/01";
    }

    public static class CurrencyTestDataConstants {
        public static final Currency CURRENCY_GBP = SupportedCurrency.GBP.getCurrency();
        public static final Currency CURRENCY_USD = Currency.getInstance("USD");
    }
    
    public static class BudgetTestDataConstants {
        public static final BigDecimal INCOME_AMOUNT = new BigDecimal("543.21");
        public static final BigDecimal EXPENSE_AMOUNT = new BigDecimal("123.45");
        public static final BigDecimal SAVINGS_AMOUNT = INCOME_AMOUNT.subtract(EXPENSE_AMOUNT);
    }

    public static class ExpenseTestDataConstants {
        public static final String NAME = "TEST EXPENSE";
        public static final BigDecimal AMOUNT = new BigDecimal("123.45");
        public static final ExpenseCategory CATEGORY_MISCELLANEOUS = ExpenseCategory.MISCELLANEOUS;
        public static final TransactionType TYPE_ONE_TIME = TransactionType.ONE_TIME;

        // -- Updated Values --
        public static final String NAME_UPDATED = "TEST EXPENSE UPDATED";
        public static final ExpenseCategory CATEGORY_UPDATED = ExpenseCategory.HOUSING;
        public static final TransactionType TYPE_UPDATED = TransactionType.RECURRING;
    }

    public static class IncomeTestDataConstants {
        public static final String NAME = "TEST INCOME";
        public static final String SOURCE = "TEST SOURCE";
        public static final BigDecimal AMOUNT = new BigDecimal("123.45");
        public static final TransactionType TYPE_ONE_TIME = TransactionType.ONE_TIME;

        // -- Updated Values --
        public static final String NAME_UPDATED = "TEST INCOME UPDATED";
        public static final String SOURCE_UPDATED = "TEST SOURCE UPDATED";
        public static final TransactionType TYPE_UPDATED = TransactionType.RECURRING;
    }

    public static class MoneyDtoTestDataConstants {
        // -- BigDecimal --
        public static final BigDecimal AMOUNT = new BigDecimal("123.45");
        public static final BigDecimal AMOUNT_THREE_DECIMALS = new BigDecimal("123.456");
        public static final BigDecimal AMOUNT_TWO_DECIMALS = new BigDecimal("123.46");
        public static final BigDecimal AMOUNT_LARGE = new BigDecimal("999999999999999.99");

        // -- String --
        public static final String AMOUNT_THREE_DECIMALS_STRING = "123.456";
        public static final String AMOUNT_ONE_HUNDRED_STRING = "100.00";
        public static final String AMOUNT_ONE_HUNDRED_POUNDS_STRING = "£100.00";

        // -- Double --
        public static final double AMOUNT_THREE_DECIMALS_DOUBLE = 123.456;

        // -- Arithmetic Values --
        public static final BigDecimal AMOUNT_ONE_HUNDRED_FIFTY = new BigDecimal("150.00");
        public static final BigDecimal AMOUNT_ONE_HUNDRED = new BigDecimal("100.00");
        public static final BigDecimal AMOUNT_NINETY_NINE_POINT_NINETY_NINE = new BigDecimal("99.99");
        public static final BigDecimal AMOUNT_FIFTY = new BigDecimal("50.00");
        public static final BigDecimal AMOUNT_THREE = new BigDecimal("3");
        public static final BigDecimal AMOUNT_ONE_POINT_FIVE = new BigDecimal("1.5");
        public static final BigDecimal AMOUNT_THIRTY_THREE_RECURRING = new BigDecimal("33.33");
    }

    public static class EnumExceptionUtilsTestDataConstants {
        public static final String ERROR_MESSAGE_ENUM_CONSTANT = "BLUEE";
        public static final String ERROR_MESSAGE_INVALID_ENUM_VALUE = "YELLOW";
        public static final String ERROR_MESSAGE_INVALID_ENUM_FIELD = "color";
        public static final String ERROR_MESSAGE_INVALID_FIELD_NAME = "invalidField";
    }

    public static class StringUtilsTestDataConstants {
        public static final String TEST_STRING = "test string";
        public static final String TEST_STRING_CAPITALIZED = "TEST STRING";
    }
}