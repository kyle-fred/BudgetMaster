package com.budgetmaster.testsupport.assertions.model;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetModelAssertions {

    private final Budget actual;

    public BudgetModelAssertions(Budget actual) {
        this.actual = actual;
    }

    public static BudgetModelAssertions assertBudget(Budget actual) {
        assertNotNull(actual);
        return new BudgetModelAssertions(actual);
    }

    public BudgetModelAssertions hasTotalIncome(BigDecimal expectedTotalIncome) {
        assertEquals(expectedTotalIncome, actual.getTotalIncome());
        return this;
    }

    public BudgetModelAssertions hasTotalExpense(BigDecimal expectedTotalExpense) {
        assertEquals(expectedTotalExpense, actual.getTotalExpense());
        return this;
    }

    public BudgetModelAssertions hasSavings(BigDecimal expectedSavings) {
        assertEquals(expectedSavings, actual.getSavings());
        return this;
    }

    public BudgetModelAssertions hasCurrency(Currency expectedCurrency) {
        assertEquals(expectedCurrency, actual.getCurrency());
        return this;
    }

    public BudgetModelAssertions hasMonth(YearMonth expectedMonth) {
        assertEquals(expectedMonth, actual.getMonth());
        return this;
    }
    
    public BudgetModelAssertions isInitialisedBudget() {
        return hasTotalIncome(BudgetConstants.ZeroValues.TOTAL_INCOME)
            .hasTotalExpense(BudgetConstants.ZeroValues.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.ZeroValues.SAVINGS)
            .hasCurrency(BudgetConstants.Default.CURRENCY)
            .hasMonth(BudgetConstants.Default.YEAR_MONTH);
    }

    public BudgetModelAssertions isDefaultBudget() {
        return hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME)
            .hasTotalExpense(BudgetConstants.Default.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.Default.SAVINGS)
            .hasCurrency(BudgetConstants.Default.CURRENCY)
            .hasMonth(BudgetConstants.Default.YEAR_MONTH);
    }

    public BudgetModelAssertions isUpdatedBudget() {
        return hasTotalIncome(BudgetConstants.Updated.TOTAL_INCOME)
            .hasTotalExpense(BudgetConstants.Updated.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.Updated.SAVINGS)
            .hasCurrency(BudgetConstants.Default.CURRENCY)
            .hasMonth(BudgetConstants.Updated.YEAR_MONTH);
    }
    
}
