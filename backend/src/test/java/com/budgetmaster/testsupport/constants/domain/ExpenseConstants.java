package com.budgetmaster.testsupport.constants.domain;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.application.enums.ExpenseCategory;
import com.budgetmaster.application.enums.TransactionType;

public final class ExpenseConstants {
  private ExpenseConstants() {}

  public static final class Default {
    private Default() {}

    public static final String NAME = "TEST EXPENSE";
    public static final BigDecimal AMOUNT = new BigDecimal("500.00");
    public static final Currency CURRENCY = Currency.getInstance("GBP");
    public static final ExpenseCategory CATEGORY = ExpenseCategory.MISCELLANEOUS;
    public static final TransactionType TYPE = TransactionType.ONE_TIME;
    public static final int YEAR = 2000;
    public static final int MONTH = 1;
    public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
    public static final String YEAR_MONTH_STRING = YEAR_MONTH.toString();
    public static final Long ID = 1L;
  }

  public static final class Updated {
    private Updated() {}

    public static final String NAME = "UPDATED EXPENSE";
    public static final BigDecimal AMOUNT = new BigDecimal("750.00");
    public static final TransactionType TYPE = TransactionType.RECURRING;
    public static final ExpenseCategory CATEGORY = ExpenseCategory.GIFTS_AND_DONATIONS;
    public static final int YEAR = 1999;
    public static final int MONTH = 12;
    public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
    public static final String YEAR_MONTH_STRING = YEAR_MONTH.toString();
  }

  public static final class Invalid {
    private Invalid() {}

    public static final String YEAR_MONTH = "1999-13";
    public static final String YEAR_MONTH_FORMAT = "1999/12";
  }

  public static final class NonExistent {
    private NonExistent() {}

    public static final Long ID = 999999L;
    public static final int YEAR = 3000;
    public static final int MONTH = 1;
    public static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, MONTH);
    public static final String YEAR_MONTH_STRING = YEAR_MONTH.toString();
  }
}
