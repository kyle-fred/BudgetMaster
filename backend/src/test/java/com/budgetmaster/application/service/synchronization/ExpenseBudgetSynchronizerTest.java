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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(JacksonConfig.class)
@DisplayName("Expense Budget Synchronizer Tests")
class ExpenseBudgetSynchronizerTest {

    private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
    private final ExpenseBudgetSynchronizer expenseBudgetSynchronizer = new ExpenseBudgetSynchronizer(budgetRepository);

    private Expense defaultExpense;
    private Budget defaultBudget;

    @BeforeEach
    void setUp() {
        defaultExpense = ExpenseBuilder.defaultExpense().build();
        defaultBudget = BudgetBuilder.defaultBudget().build();
    }

    @Nested
    @DisplayName("Apply Operations")
    class ApplyOperations {
        
        @Test
        @DisplayName("Should update existing budget when applying expense")
        void apply_withExistingBudget_updatesBudget() {
            when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
                .thenReturn(Optional.of(defaultBudget));
            when(budgetRepository.save(any(Budget.class)))
                .thenReturn(defaultBudget);

            expenseBudgetSynchronizer.apply(defaultExpense);

            BudgetModelAssertions.assertBudget(defaultBudget)
                .hasTotalExpense(BudgetConstants.AfterAddExpense_WhenBudgetExists.TOTAL_EXPENSE)
                .hasSavings(BudgetConstants.AfterAddExpense_WhenBudgetExists.SAVINGS);

            verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
            verify(budgetRepository).save(defaultBudget);
        }

        @Test
        @DisplayName("Should create new budget when applying expense to non-existent budget")
        void apply_withNoExistingBudget_createsNewBudget() {
            when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
                .thenReturn(Optional.empty());
            when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            expenseBudgetSynchronizer.apply(defaultExpense);

            ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
            verify(budgetRepository).save(captor.capture());
            Budget budget = captor.getValue();

            BudgetModelAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.AfterAddExpense_WhenNoBudgetExists.TOTAL_EXPENSE)
                .hasSavings(BudgetConstants.AfterAddExpense_WhenNoBudgetExists.SAVINGS);

            verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        }
    }

    @Nested
    @DisplayName("Reapply Operations")
    class ReapplyOperations {
        
        @Test
        @DisplayName("Should update budget when reapplying expense in same month")
        void reapply_withSameMonth_updatesBudget() {
            Expense updatedExpense = ExpenseBuilder.updatedExpense()
                .withMonth(ExpenseConstants.Default.YEAR_MONTH)
                .build();

            when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
                .thenReturn(Optional.of(defaultBudget));
            when(budgetRepository.save(any(Budget.class)))
                .thenReturn(defaultBudget);

            expenseBudgetSynchronizer.reapply(defaultExpense, updatedExpense);

            BudgetModelAssertions.assertBudget(defaultBudget)
                .hasTotalExpense(BudgetConstants.AfterReapplyExpense_SameMonth.TOTAL_EXPENSE)
                .hasSavings(BudgetConstants.AfterReapplyExpense_SameMonth.SAVINGS);

            verify(budgetRepository, times(2)).findByMonth(BudgetConstants.Default.YEAR_MONTH);
            verify(budgetRepository).save(defaultBudget);
        }

        @Test
        @DisplayName("Should update both budgets when reapplying expense to different month")
        void reapply_withDifferentMonth_updatesBothBudgets() {
            Expense updatedExpense = ExpenseBuilder.updatedExpense()
                .withMonth(ExpenseConstants.Updated.YEAR_MONTH)
                .build();

            Budget newBudget = BudgetBuilder.zeroedBudget()
                .withMonth(BudgetConstants.Updated.YEAR_MONTH)
                .build();

            when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
                .thenReturn(Optional.of(defaultBudget));
            when(budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH))
                .thenReturn(Optional.of(newBudget));
            when(budgetRepository.save(any(Budget.class)))
                .thenReturn(defaultBudget)
                .thenReturn(newBudget);

            expenseBudgetSynchronizer.reapply(defaultExpense, updatedExpense);

            BudgetModelAssertions.assertBudget(defaultBudget)
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
        @DisplayName("Should throw exception when original budget not found during reapply")
        void reapply_withOriginalBudgetNotFound_throwsException() {
            Expense updatedExpense = ExpenseBuilder.updatedExpense()
                .withMonth(ExpenseConstants.Updated.YEAR_MONTH)
                .build();

            when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
                .thenReturn(Optional.empty());

            assertThrows(BudgetNotFoundException.class, () -> 
                expenseBudgetSynchronizer.reapply(defaultExpense, updatedExpense)
            );

            verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
            verify(budgetRepository, never()).save(any(Budget.class));
        }
    }

    @Nested
    @DisplayName("Retract Operations")
    class RetractOperations {
        
        @Test
        @DisplayName("Should update budget when retracting expense")
        void retract_withExistingBudget_updatesBudget() {
            when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
                .thenReturn(Optional.of(defaultBudget));
            when(budgetRepository.save(any(Budget.class)))
                .thenReturn(defaultBudget);

            expenseBudgetSynchronizer.retract(defaultExpense);

            BudgetModelAssertions.assertBudget(defaultBudget)
                .hasTotalExpense(BudgetConstants.AfterRetractExpense.TOTAL_EXPENSE)
                .hasSavings(BudgetConstants.AfterRetractExpense.SAVINGS);

            verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
            verify(budgetRepository).save(defaultBudget);
        }

        @Test
        @DisplayName("Should throw exception when budget not found during retract")
        void retract_withBudgetNotFound_throwsException() {
            when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
                .thenReturn(Optional.empty());

            assertThrows(BudgetNotFoundException.class, () -> 
                expenseBudgetSynchronizer.retract(defaultExpense)
            );

            verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
            verify(budgetRepository, never()).save(any(Budget.class));
        }
    }
}
