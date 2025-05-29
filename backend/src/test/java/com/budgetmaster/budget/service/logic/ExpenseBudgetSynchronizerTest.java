package com.budgetmaster.budget.service.logic;

import java.util.Optional;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.service.synchronization.ExpenseBudgetSynchronizer;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.testsupport.budget.builder.BudgetBuilder;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.expense.builder.ExpenseBuilder;
import com.budgetmaster.testsupport.expense.constants.ExpenseConstants;
import com.budgetmaster.testsupport.expense.factory.ExpenseFactory;

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
        testExpense = ExpenseFactory.createDefaultExpense();
        testBudget = BudgetBuilder.defaultBudget().build();
    }

    @Test
    void apply_ExistingBudget_UpdatesBudget() {
        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        expenseBudgetSynchronizer.apply(testExpense);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BudgetConstants.AfterAddExpense_WhenBudgetExists.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetConstants.AfterAddExpense_WhenBudgetExists.SAVINGS, testBudget.getSavings());
    }

    @Test
    void apply_NoExistingBudget_CreatesNewBudget() {
        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        expenseBudgetSynchronizer.apply(testExpense);

        ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
        verify(budgetRepository).save(captor.capture());
        Budget budget = captor.getValue();

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        assertEquals(BudgetConstants.AfterAddExpense_WhenNoBudgetExists.TOTAL_EXPENSE, budget.getTotalExpense());
        assertEquals(BudgetConstants.AfterAddExpense_WhenNoBudgetExists.SAVINGS, budget.getSavings());
    }

    @Test
    void reapply_SameMonth_UpdatesBudget() {
        Expense updatedExpense = ExpenseBuilder.updatedExpense()
            .withMonth(ExpenseConstants.Default.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        expenseBudgetSynchronizer.reapply(testExpense, updatedExpense);

        verify(budgetRepository, times(2)).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BudgetConstants.AfterReapplyExpense_SameMonth.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetConstants.AfterReapplyExpense_SameMonth.SAVINGS, testBudget.getSavings());
    }

    @Test
    void reapply_DifferentMonth_UpdatesBothBudgets() {
        Expense updatedExpense = ExpenseBuilder.updatedExpense()
            .withMonth(ExpenseConstants.Updated.YEAR_MONTH)
            .build();

        Budget newBudget = BudgetBuilder.zeroedBudget()
            .withMonth(BudgetConstants.Updated.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH))
            .thenReturn(Optional.of(newBudget));

        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget)
            .thenReturn(newBudget);

        expenseBudgetSynchronizer.reapply(testExpense, updatedExpense);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).findByMonth(BudgetConstants.Updated.YEAR_MONTH);
        verify(budgetRepository).save(newBudget);
        assertEquals(BudgetConstants.AfterReapplyExpense_DifferentMonth.ExistingBudget.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetConstants.AfterReapplyExpense_DifferentMonth.ExistingBudget.SAVINGS, testBudget.getSavings());
        assertEquals(BudgetConstants.AfterReapplyExpense_DifferentMonth.NewBudget.TOTAL_EXPENSE, newBudget.getTotalExpense());
        assertEquals(BudgetConstants.AfterReapplyExpense_DifferentMonth.NewBudget.SAVINGS, newBudget.getSavings());
    }

    @Test
    void reapply_OriginalBudgetNotFound_ThrowsException() {
        Expense updatedExpense = ExpenseBuilder.updatedExpense()
            .withMonth(ExpenseConstants.Updated.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            expenseBudgetSynchronizer.reapply(testExpense, updatedExpense)
        );

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void retract_ExistingBudget_UpdatesBudget() {
        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        expenseBudgetSynchronizer.retract(testExpense);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BudgetConstants.AfterRetractExpense.TOTAL_EXPENSE, testBudget.getTotalExpense());
        assertEquals(BudgetConstants.AfterRetractExpense.SAVINGS, testBudget.getSavings());
    }

    @Test
    void retract_BudgetNotFound_ThrowsException() {
        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            expenseBudgetSynchronizer.retract(testExpense)
        );

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository, never()).save(any(Budget.class));
    }
}
