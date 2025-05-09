package com.budgetmaster.budget.service.logic;

import java.math.BigDecimal;
import java.util.Optional;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.test.builder.BudgetTestBuilder;
import com.budgetmaster.test.builder.ExpenseTestBuilder;
import com.budgetmaster.test.constants.TestData.BudgetTestConstants;
import com.budgetmaster.test.constants.TestData.ExpenseTestConstants;
import com.budgetmaster.test.factory.ExpenseTestFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(JacksonConfig.class)
public class ExpenseBudgetSynchronizerTest {
    // -- Dependencies --
    private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
    private final ExpenseBudgetSynchronizer expenseBudgetSynchronizer = new ExpenseBudgetSynchronizer(budgetRepository);

    // -- Test Objects --
    private Expense testExpense;
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testExpense = ExpenseTestFactory.createDefaultExpense();
        testBudget = BudgetTestBuilder.defaultBudget()
            .withTotalIncome(BudgetTestConstants.Default.TOTAL_INCOME)
            .withTotalExpense(BudgetTestConstants.Default.TOTAL_EXPENSE)
            .withSavings(BudgetTestConstants.Default.SAVINGS)
            .build();
    }

    @Test
    void apply_ExistingBudget_UpdatesBudget() {
        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        expenseBudgetSynchronizer.apply(testExpense);

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BudgetTestConstants.AfterExpenseApplied.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetTestConstants.AfterExpenseApplied.SAVINGS, testBudget.getSavings());
    }

    @Test
    void apply_NoExistingBudget_CreatesNewBudget() {
        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        expenseBudgetSynchronizer.apply(testExpense);

        ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
        verify(budgetRepository).save(captor.capture());
        Budget budget = captor.getValue();

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        assertEquals(BudgetTestConstants.Default.TOTAL_EXPENSE, budget.getTotalExpense());
        assertEquals(BudgetTestConstants.Default.SAVINGS.negate(), budget.getSavings());
    }

    @Test
    void reapply_SameMonth_UpdatesBudget() {
        Expense updatedExpense = ExpenseTestBuilder.updatedExpense()
            .withMonth(ExpenseTestConstants.Default.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        expenseBudgetSynchronizer.reapply(testExpense, updatedExpense);

        verify(budgetRepository, times(2)).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BudgetTestConstants.AfterExpenseUpdatedSameMonth.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetTestConstants.AfterExpenseUpdatedSameMonth.SAVINGS, testBudget.getSavings());
    }

    @Test
    void reapply_DifferentMonth_UpdatesBothBudgets() {
        Expense updatedExpense = ExpenseTestBuilder.updatedExpense()
            .withMonth(ExpenseTestConstants.Updated.YEAR_MONTH)
            .build();

        Budget newBudget = BudgetTestBuilder.defaultBudget()
            .withId(testBudget.getId() + 1)
            .withTotalIncome(BigDecimal.ZERO)
            .withTotalExpense(BigDecimal.ZERO)
            .withSavings(BigDecimal.ZERO)
            .withMonth(BudgetTestConstants.Updated.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.findByMonth(BudgetTestConstants.Updated.YEAR_MONTH))
            .thenReturn(Optional.of(newBudget));

        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget)
            .thenReturn(newBudget);

        expenseBudgetSynchronizer.reapply(testExpense, updatedExpense);

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).findByMonth(ExpenseTestConstants.Updated.YEAR_MONTH);
        verify(budgetRepository).save(newBudget);
        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalExpense());
        assertEquals(BudgetTestConstants.Default.TOTAL_INCOME, testBudget.getSavings());
        assertEquals(BudgetTestConstants.AfterExpenseMovedToDifferentMonth.TOTAL_EXPENSE, newBudget.getTotalExpense());
        assertEquals(BudgetTestConstants.AfterExpenseMovedToDifferentMonth.SAVINGS.negate(), newBudget.getSavings());
    }

    @Test
    void reapply_OriginalBudgetNotFound_ThrowsException() {
        Expense updatedExpense = ExpenseTestBuilder.updatedExpense()
            .withMonth(ExpenseTestConstants.Updated.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            expenseBudgetSynchronizer.reapply(testExpense, updatedExpense)
        );

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void retract_ExistingBudget_UpdatesBudget() {
        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        expenseBudgetSynchronizer.retract(testExpense);

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalExpense());
        assertEquals(BudgetTestConstants.Default.TOTAL_INCOME, testBudget.getSavings());
    }

    @Test
    void retract_BudgetNotFound_ThrowsException() {
        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            expenseBudgetSynchronizer.retract(testExpense)
        );

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository, never()).save(any(Budget.class));
    }
}
