package com.budgetmaster.application.model;

import com.budgetmaster.testsupport.assertions.model.BudgetModelAssertions;
import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

public class BudgetTest {

    private Budget budget;

    @BeforeEach
    void setUp() {
        budget = BudgetBuilder.defaultBudget().build();
    }
    
    @Test
    void testOf_WithMonthAndCurrency_ReturnsInitialisedBudget() {
        budget = Budget.of(BudgetConstants.Default.YEAR_MONTH, BudgetConstants.Default.CURRENCY);

        BudgetModelAssertions.assertBudget(budget)
            .isInitialisedBudget();
    }

    @Test
    void testAddIncome_WithValidAmount_IncreasesTotalIncome() {
        budget.addIncome(BudgetConstants.Default.TOTAL_INCOME);

        BudgetModelAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.AfterAddIncome_WhenBudgetExists.TOTAL_INCOME)
            .hasSavings(BudgetConstants.AfterAddIncome_WhenBudgetExists.SAVINGS);
    }

    @Test
    void testSubtractIncome_WithValidAmount_DecreasesTotalIncome() {
        budget.subtractIncome(BudgetConstants.Default.TOTAL_INCOME);

        BudgetModelAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.AfterSubtractIncome_WhenBudgetExists.TOTAL_INCOME)
            .hasSavings(BudgetConstants.AfterSubtractIncome_WhenBudgetExists.SAVINGS);
    }

    @Test
    void testAddExpense_WithValidAmount_IncreasesTotalExpense() {
        budget.addExpense(BudgetConstants.Default.TOTAL_EXPENSE);

        BudgetModelAssertions.assertBudget(budget)
            .hasTotalExpense(BudgetConstants.AfterAddExpense_WhenBudgetExists.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.AfterAddExpense_WhenBudgetExists.SAVINGS);
    }

    @Test
    void testSubtractExpense_WithValidAmount_DecreasesTotalExpense() {
        budget.subtractExpense(BudgetConstants.Default.TOTAL_EXPENSE);

        BudgetModelAssertions.assertBudget(budget)
            .hasTotalExpense(BudgetConstants.AfterSubtractExpense_WhenBudgetExists.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.AfterSubtractExpense_WhenBudgetExists.SAVINGS);
    }
}
