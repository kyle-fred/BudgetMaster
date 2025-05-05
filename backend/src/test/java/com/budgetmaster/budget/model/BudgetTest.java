package com.budgetmaster.budget.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.test.constants.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BudgetTest {
    // -- Test Data --
    private static final YearMonth testMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
    private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
    private static final BigDecimal testIncome = TestData.IncomeTestDataConstants.AMOUNT;
    private static final BigDecimal testExpense = TestData.ExpenseTestDataConstants.AMOUNT;
    // -- Test Objects --
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testBudget = Budget.of(testMonth, testCurrency);
    }

    @Test
    void testOf_WithMonthAndCurrency_ReturnsBudget() {
        assertEquals(testMonth, testBudget.getMonth());
        assertEquals(testCurrency, testBudget.getCurrency());
    }

    @Test
    void testOf_WithMonthAndCurrency_SetsZeroValues() {
        assertEquals(BigDecimal.ZERO, testBudget.getTotalIncome());
        assertEquals(BigDecimal.ZERO, testBudget.getTotalExpense());
        assertEquals(BigDecimal.ZERO, testBudget.getSavings());
    }

    @Test
    void testAddIncome_WithValidAmount_IncreasesTotalIncome() {
        testBudget.addIncome(testIncome);
        assertEquals(testIncome, testBudget.getTotalIncome());
        assertEquals(testIncome, testBudget.getSavings());
    }

    @Test
    void testSubtractIncome_WithValidAmount_DecreasesTotalIncome() {
        testBudget.subtractIncome(testIncome);
        assertEquals(testIncome.negate(), testBudget.getTotalIncome());
        assertEquals(testIncome.negate(), testBudget.getSavings());
    }

    @Test
    void testAddExpense_WithValidAmount_IncreasesTotalExpense() {
        testBudget.addExpense(testExpense);
        assertEquals(testExpense, testBudget.getTotalExpense());
    }

    @Test
    void testSubtractExpense_WithValidAmount_DecreasesTotalExpense() {
        testBudget.subtractExpense(testExpense);
        assertEquals(testExpense.negate(), testBudget.getTotalExpense());
    }
}
