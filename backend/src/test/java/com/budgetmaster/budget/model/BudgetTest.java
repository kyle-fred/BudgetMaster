package com.budgetmaster.budget.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.test.constants.TestData;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BudgetTest {
    // -- Test Data --
    private static final YearMonth testMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
    private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;

    @Test
    void testOf_WithMonthAndCurrency_ReturnsBudget() {
        Budget budget = Budget.of(testMonth, testCurrency);
        assertEquals(testMonth, budget.getMonth());
        assertEquals(testCurrency, budget.getCurrency());
    }

    @Test
    void testOf_WithMonthAndCurrency_SetsZeroValues() {
        Budget budget = Budget.of(testMonth, testCurrency);
        assertEquals(BigDecimal.ZERO, budget.getTotalIncome());
        assertEquals(BigDecimal.ZERO, budget.getTotalExpense());
        assertEquals(BigDecimal.ZERO, budget.getSavings());
    }
}
