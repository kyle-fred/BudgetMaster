package com.budgetmaster.application.service.synchronization;

import java.util.Optional;

import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.assertions.model.BudgetModelAssertions;
import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(JacksonConfig.class)
public class ExpenseBudgetSynchronizerTest {

    private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
    private final ExpenseBudgetSynchronizer expenseBudgetSynchronizer = new ExpenseBudgetSynchronizer(budgetRepository);

    private Expense testExpense;
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testExpense = ExpenseBuilder.defaultExpense().build();
        testBudget = BudgetBuilder.defaultBudget().build();
    }

    @Test
    void apply_ExistingBudget_UpdatesBudget() {
        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        expenseBudgetSynchronizer.apply(testExpense);

        BudgetModelAssertions.assertBudget(testBudget)
            .hasTotalExpense(BudgetConstants.AfterAddExpense_WhenBudgetExists.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.AfterAddExpense_WhenBudgetExists.SAVINGS);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
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

        BudgetModelAssertions.assertBudget(budget)
            .hasTotalExpense(BudgetConstants.AfterAddExpense_WhenNoBudgetExists.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.AfterAddExpense_WhenNoBudgetExists.SAVINGS);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
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

        BudgetModelAssertions.assertBudget(testBudget)
            .hasTotalExpense(BudgetConstants.AfterReapplyExpense_SameMonth.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.AfterReapplyExpense_SameMonth.SAVINGS);

        verify(budgetRepository, times(2)).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
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

        BudgetModelAssertions.assertBudget(testBudget)
            .hasTotalExpense(BudgetConstants.AfterReapplyExpense_DifferentMonth.ExistingBudget.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.AfterReapplyExpense_DifferentMonth.ExistingBudget.SAVINGS);

        BudgetModelAssertions.assertBudget(newBudget)
            .hasTotalExpense(BudgetConstants.AfterReapplyExpense_DifferentMonth.NewBudget.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.AfterReapplyExpense_DifferentMonth.NewBudget.SAVINGS);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).findByMonth(BudgetConstants.Updated.YEAR_MONTH);
        verify(budgetRepository).save(newBudget);
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

        BudgetModelAssertions.assertBudget(testBudget)
            .hasTotalExpense(BudgetConstants.AfterRetractExpense.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.AfterRetractExpense.SAVINGS);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
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
