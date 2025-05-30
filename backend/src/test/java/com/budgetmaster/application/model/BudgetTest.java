package com.budgetmaster.application.model;

import com.budgetmaster.testsupport.builder.BudgetBuilder;
import com.budgetmaster.testsupport.builder.BudgetFactory;
import com.budgetmaster.testsupport.builder.ExpenseFactory;
import com.budgetmaster.testsupport.builder.IncomeFactory;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BudgetTest {
    
    @Test
    void testOf_WithMonthAndCurrency_ReturnsBudget() {
        Budget testBudget = BudgetFactory.createDefaultBudget();

        assertNotNull(testBudget);
        assertEquals(BudgetConstants.Default.YEAR_MONTH, testBudget.getMonth());
        assertEquals(BudgetConstants.Default.CURRENCY, testBudget.getCurrency());
    }

    @Test
    void testOf_WithMonthAndCurrency_SetsZeroValues() {
        Budget testBudget = BudgetFactory.createDefaultBudget();

        assertEquals(BudgetConstants.ZeroValues.TOTAL_INCOME, testBudget.getTotalIncome());
        assertEquals(BudgetConstants.ZeroValues.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetConstants.ZeroValues.SAVINGS, testBudget.getSavings());
    }

    @Test
    void testAddIncome_WithValidAmount_IncreasesTotalIncome() {
        Budget testBudget = BudgetBuilder.defaultBudget().build();
        Income testIncome = IncomeFactory.createDefaultIncome();

        testBudget.addIncome(testIncome.getMoney().getAmount());

        assertEquals(BudgetConstants.AfterAddIncome_WhenBudgetExists.TOTAL_INCOME, testBudget.getTotalIncome());
        assertEquals(BudgetConstants.AfterAddIncome_WhenBudgetExists.SAVINGS, testBudget.getSavings());
    }

    @Test
    void testSubtractIncome_WithValidAmount_DecreasesTotalIncome() {
        Budget testBudget = BudgetBuilder.defaultBudget().build();

        testBudget.subtractIncome(BudgetConstants.Default.TOTAL_INCOME);

        assertEquals(BudgetConstants.AfterSubtractIncome_WhenBudgetExists.TOTAL_INCOME, testBudget.getTotalIncome());
        assertEquals(BudgetConstants.AfterSubtractIncome_WhenBudgetExists.SAVINGS, testBudget.getSavings());
    }

    @Test
    void testAddExpense_WithValidAmount_IncreasesTotalExpense() {
        Budget testBudget = BudgetBuilder.defaultBudget().build();
        Expense testExpense = ExpenseFactory.createDefaultExpense();

        testBudget.addExpense(testExpense.getMoney().getAmount());

        assertEquals(BudgetConstants.AfterAddExpense_WhenBudgetExists.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetConstants.AfterAddExpense_WhenBudgetExists.SAVINGS, testBudget.getSavings());
    }

    @Test
    void testSubtractExpense_WithValidAmount_DecreasesTotalExpense() {
        Budget testBudget = BudgetBuilder.defaultBudget().build();
        Expense testExpense = ExpenseFactory.createDefaultExpense();

        testBudget.subtractExpense(testExpense.getMoney().getAmount());

        assertEquals(BudgetConstants.AfterSubtractExpense_WhenBudgetExists.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetConstants.AfterSubtractExpense_WhenBudgetExists.SAVINGS, testBudget.getSavings());
    }
}
