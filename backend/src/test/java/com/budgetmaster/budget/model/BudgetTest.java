package com.budgetmaster.budget.model;

import java.math.BigDecimal;

import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.testsupport.budget.builder.BudgetBuilder;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.budget.factory.BudgetFactory;
import com.budgetmaster.testsupport.expense.factory.ExpenseFactory;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
import com.budgetmaster.testsupport.income.factory.IncomeFactory;

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

        assertEquals(BigDecimal.ZERO, testBudget.getTotalIncome());
        assertEquals(BigDecimal.ZERO, testBudget.getTotalExpense());
        assertEquals(BigDecimal.ZERO, testBudget.getSavings());
    }

    @Test
    void testAddIncome_WithValidAmount_IncreasesTotalIncome() {
        Budget testBudget = BudgetFactory.createDefaultBudget();
        Income testIncome = IncomeFactory.createDefaultIncome();

        testBudget.addIncome(testIncome.getMoney().getAmount());

        assertEquals(IncomeConstants.Default.AMOUNT, testBudget.getTotalIncome());
        assertEquals(IncomeConstants.Default.AMOUNT, testBudget.getSavings());
    }

    @Test
    void testSubtractIncome_WithValidAmount_DecreasesTotalIncome() {
        Budget testBudget = BudgetBuilder.defaultBudget()
            .withTotalIncome(BudgetConstants.Default.TOTAL_INCOME)
            .withTotalExpense(BigDecimal.ZERO)
            .withSavings(BudgetConstants.Default.TOTAL_INCOME)
            .build();

        testBudget.subtractIncome(IncomeConstants.Default.AMOUNT);

        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalIncome());
        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getSavings());
    }

    @Test
    void testAddExpense_WithValidAmount_IncreasesTotalExpense() {
        Budget testBudget = BudgetBuilder.defaultBudget()
            .withTotalIncome(BudgetConstants.Default.TOTAL_INCOME)
            .withTotalExpense(BigDecimal.ZERO)
            .withSavings(BudgetConstants.Default.TOTAL_INCOME)
            .build();

        Expense testExpense = ExpenseFactory.createDefaultExpense();

        testBudget.addExpense(testExpense.getMoney().getAmount());

        assertEquals(BudgetConstants.Default.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetConstants.Default.SAVINGS, testBudget.getSavings());
    }

    @Test
    void testSubtractExpense_WithValidAmount_DecreasesTotalExpense() {
        Budget testBudget = BudgetBuilder.defaultBudget()
            .withTotalIncome(BudgetConstants.Default.TOTAL_INCOME)
            .withTotalExpense(BudgetConstants.Default.TOTAL_EXPENSE)
            .withSavings(BudgetConstants.Default.SAVINGS)
            .build();

        Expense testExpense = ExpenseFactory.createDefaultExpense();

        testBudget.subtractExpense(testExpense.getMoney().getAmount());

        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalExpense());
        assertEquals(BudgetConstants.Default.TOTAL_INCOME, testBudget.getSavings());
    }
}
