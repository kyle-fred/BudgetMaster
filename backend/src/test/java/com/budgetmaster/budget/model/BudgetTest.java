package com.budgetmaster.budget.model;

import java.math.BigDecimal;

import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.test.builder.BudgetTestBuilder;
import com.budgetmaster.test.constants.TestData.BudgetTestConstants;
import com.budgetmaster.test.constants.TestData.IncomeTestConstants;
import com.budgetmaster.test.factory.BudgetTestFactory;
import com.budgetmaster.test.factory.ExpenseTestFactory;
import com.budgetmaster.test.factory.IncomeTestFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BudgetTest {

    @Test
    void testOf_WithMonthAndCurrency_ReturnsBudget() {
        Budget testBudget = BudgetTestFactory.createDefaultBudget();

        assertNotNull(testBudget);
        assertEquals(BudgetTestConstants.Default.YEAR_MONTH, testBudget.getMonth());
        assertEquals(BudgetTestConstants.Default.CURRENCY, testBudget.getCurrency());
    }

    @Test
    void testOf_WithMonthAndCurrency_SetsZeroValues() {
        Budget testBudget = BudgetTestFactory.createDefaultBudget();

        assertEquals(BigDecimal.ZERO, testBudget.getTotalIncome());
        assertEquals(BigDecimal.ZERO, testBudget.getTotalExpense());
        assertEquals(BigDecimal.ZERO, testBudget.getSavings());
    }

    @Test
    void testAddIncome_WithValidAmount_IncreasesTotalIncome() {
        Budget testBudget = BudgetTestFactory.createDefaultBudget();
        Income testIncome = IncomeTestFactory.createDefaultIncome();

        testBudget.addIncome(testIncome.getMoney().getAmount());

        assertEquals(IncomeTestConstants.Default.AMOUNT, testBudget.getTotalIncome());
        assertEquals(IncomeTestConstants.Default.AMOUNT, testBudget.getSavings());
    }

    @Test
    void testSubtractIncome_WithValidAmount_DecreasesTotalIncome() {
        Budget testBudget = BudgetTestBuilder.defaultBudget()
            .withTotalIncome(BudgetTestConstants.Default.TOTAL_INCOME)
            .withTotalExpense(BigDecimal.ZERO)
            .withSavings(BudgetTestConstants.Default.TOTAL_INCOME)
            .build();

        testBudget.subtractIncome(IncomeTestConstants.Default.AMOUNT);

        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalIncome());
        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getSavings());
    }

    @Test
    void testAddExpense_WithValidAmount_IncreasesTotalExpense() {
        Budget testBudget = BudgetTestBuilder.defaultBudget()
            .withTotalIncome(BudgetTestConstants.Default.TOTAL_INCOME)
            .withTotalExpense(BigDecimal.ZERO)
            .withSavings(BudgetTestConstants.Default.TOTAL_INCOME)
            .build();

        Expense testExpense = ExpenseTestFactory.createDefaultExpense();

        testBudget.addExpense(testExpense.getMoney().getAmount());

        assertEquals(BudgetTestConstants.Default.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetTestConstants.Default.SAVINGS, testBudget.getSavings());
    }

    @Test
    void testSubtractExpense_WithValidAmount_DecreasesTotalExpense() {
        Budget testBudget = BudgetTestBuilder.defaultBudget()
            .withTotalIncome(BudgetTestConstants.Default.TOTAL_INCOME)
            .withTotalExpense(BudgetTestConstants.Default.TOTAL_EXPENSE)
            .withSavings(BudgetTestConstants.Default.SAVINGS)
            .build();

        Expense testExpense = ExpenseTestFactory.createDefaultExpense();

        testBudget.subtractExpense(testExpense.getMoney().getAmount());

        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalExpense());
        assertEquals(BudgetTestConstants.Default.TOTAL_INCOME, testBudget.getSavings());
    }
}
