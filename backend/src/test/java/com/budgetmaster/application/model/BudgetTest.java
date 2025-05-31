package com.budgetmaster.application.model;

import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;

public class BudgetTest {

    private Budget budget;

    @BeforeEach
    void setUp() {
        budget = BudgetBuilder.defaultBudget().build();
    }
    
    @Test
    void testOf_WithMonthAndCurrency_ReturnsBudget() {
        budget = Budget.of(BudgetConstants.Default.YEAR_MONTH, BudgetConstants.Default.CURRENCY);

        assertNotNull(budget);
        assertEquals(BudgetConstants.Default.YEAR_MONTH, budget.getMonth());
        assertEquals(BudgetConstants.Default.CURRENCY, budget.getCurrency());
    }

    @Test
    void testOf_WithMonthAndCurrency_SetsZeroValues() {
        budget = Budget.of(BudgetConstants.Default.YEAR_MONTH, BudgetConstants.Default.CURRENCY);

        assertEquals(BudgetConstants.ZeroValues.TOTAL_INCOME, budget.getTotalIncome());
        assertEquals(BudgetConstants.ZeroValues.TOTAL_EXPENSE, budget.getTotalExpense());
        assertEquals(BudgetConstants.ZeroValues.SAVINGS, budget.getSavings());
    }

    @Test
    void testAddIncome_WithValidAmount_IncreasesTotalIncome() {
        Income testIncome = IncomeBuilder.defaultIncome().build();

        budget.addIncome(testIncome.getMoney().getAmount());

        assertEquals(BudgetConstants.AfterAddIncome_WhenBudgetExists.TOTAL_INCOME, budget.getTotalIncome());
        assertEquals(BudgetConstants.AfterAddIncome_WhenBudgetExists.SAVINGS, budget.getSavings());
    }

    @Test
    void testSubtractIncome_WithValidAmount_DecreasesTotalIncome() {
        budget.subtractIncome(BudgetConstants.Default.TOTAL_INCOME);

        assertEquals(BudgetConstants.AfterSubtractIncome_WhenBudgetExists.TOTAL_INCOME, budget.getTotalIncome());
        assertEquals(BudgetConstants.AfterSubtractIncome_WhenBudgetExists.SAVINGS, budget.getSavings());
    }

    @Test
    void testAddExpense_WithValidAmount_IncreasesTotalExpense() {
        Expense testExpense = ExpenseBuilder.defaultExpense().build();

        budget.addExpense(testExpense.getMoney().getAmount());

        assertEquals(BudgetConstants.AfterAddExpense_WhenBudgetExists.TOTAL_EXPENSE, budget.getTotalExpense());
        assertEquals(BudgetConstants.AfterAddExpense_WhenBudgetExists.SAVINGS, budget.getSavings());
    }

    @Test
    void testSubtractExpense_WithValidAmount_DecreasesTotalExpense() {
        Expense testExpense = ExpenseBuilder.defaultExpense().build();

        budget.subtractExpense(testExpense.getMoney().getAmount());

        assertEquals(BudgetConstants.AfterSubtractExpense_WhenBudgetExists.TOTAL_EXPENSE, budget.getTotalExpense());
        assertEquals(BudgetConstants.AfterSubtractExpense_WhenBudgetExists.SAVINGS, budget.getSavings());
    }
}
