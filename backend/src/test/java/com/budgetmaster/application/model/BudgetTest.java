package com.budgetmaster.application.model;

import com.budgetmaster.testsupport.assertions.model.BudgetModelAssertions;
import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Budget Model Tests")
class BudgetTest {

    private Budget budget;

    @BeforeEach
    void setUp() {
        budget = BudgetBuilder.defaultBudget().build();
    }

    @Nested
    @DisplayName("Budget Initialization")
    class BudgetInitialization {
        
        @Test
        @DisplayName("Should initialize budget with month and currency")
        void of_withMonthAndCurrency_returnsInitialisedBudget() {
            budget = Budget.of(BudgetConstants.Default.YEAR_MONTH, BudgetConstants.Default.CURRENCY);

            BudgetModelAssertions.assertBudget(budget)
                .isInitialisedBudget();
        }
    }

    @Nested
    @DisplayName("Income Management")
    class IncomeManagement {
        
        @Test
        @DisplayName("Should increase total income when adding income")
        void addIncome_withValidAmount_increasesTotalIncome() {
            budget.addIncome(BudgetConstants.Default.TOTAL_INCOME);

            BudgetModelAssertions.assertBudget(budget)
                .hasTotalIncome(BudgetConstants.AfterAddIncome_WhenBudgetExists.TOTAL_INCOME)
                .hasSavings(BudgetConstants.AfterAddIncome_WhenBudgetExists.SAVINGS);
        }

        @Test
        @DisplayName("Should decrease total income when subtracting income")
        void subtractIncome_withValidAmount_decreasesTotalIncome() {
            budget.subtractIncome(BudgetConstants.Default.TOTAL_INCOME);

            BudgetModelAssertions.assertBudget(budget)
                .hasTotalIncome(BudgetConstants.AfterSubtractIncome_WhenBudgetExists.TOTAL_INCOME)
                .hasSavings(BudgetConstants.AfterSubtractIncome_WhenBudgetExists.SAVINGS);
        }
    }

    @Nested
    @DisplayName("Expense Management")
    class ExpenseManagement {
        
        @Test
        @DisplayName("Should increase total expense when adding expense")
        void addExpense_withValidAmount_increasesTotalExpense() {
            budget.addExpense(BudgetConstants.Default.TOTAL_EXPENSE);

            BudgetModelAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.AfterAddExpense_WhenBudgetExists.TOTAL_EXPENSE)
                .hasSavings(BudgetConstants.AfterAddExpense_WhenBudgetExists.SAVINGS);
        }

        @Test
        @DisplayName("Should decrease total expense when subtracting expense")
        void subtractExpense_withValidAmount_decreasesTotalExpense() {
            budget.subtractExpense(BudgetConstants.Default.TOTAL_EXPENSE);

            BudgetModelAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.AfterSubtractExpense_WhenBudgetExists.TOTAL_EXPENSE)
                .hasSavings(BudgetConstants.AfterSubtractExpense_WhenBudgetExists.SAVINGS);
        }
    }
}
