package com.budgetmaster.budget.service.logic;

import java.math.BigDecimal;
import java.util.Optional;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.test.builder.BudgetTestBuilder;
import com.budgetmaster.test.builder.IncomeTestBuilder;
import com.budgetmaster.test.constants.TestData.BudgetTestConstants;
import com.budgetmaster.test.constants.TestData.IncomeTestConstants;
import com.budgetmaster.test.factory.IncomeTestFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(JacksonConfig.class)
public class IncomeBudgetSynchronizerTest {
    // -- Dependencies --
    private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
    private final IncomeBudgetSynchronizer incomeBudgetSynchronizer = new IncomeBudgetSynchronizer(budgetRepository);

    // -- Test Objects --
    private Income testIncome;
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testIncome = IncomeTestFactory.createDefaultIncome();
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

        incomeBudgetSynchronizer.apply(testIncome);

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BudgetTestConstants.AfterIncomeApplied.TOTAL_INCOME, testBudget.getTotalIncome());
        assertEquals(BudgetTestConstants.AfterIncomeApplied.SAVINGS, testBudget.getSavings());
    }

    @Test
    void apply_NoExistingBudget_CreatesNewBudget() {
        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        incomeBudgetSynchronizer.apply(testIncome);

		ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
		verify(budgetRepository).save(captor.capture());
		Budget budget = captor.getValue();

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        assertEquals(BudgetTestConstants.Default.TOTAL_INCOME, budget.getTotalIncome());
        assertEquals(BudgetTestConstants.Default.TOTAL_INCOME, budget.getSavings());
    }

    @Test
    void reapply_SameMonth_UpdatesBudget() {
        Income updatedIncome = IncomeTestBuilder.updatedIncome()
            .withMonth(IncomeTestConstants.Default.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.reapply(testIncome, updatedIncome);

		verify(budgetRepository, times(2)).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BudgetTestConstants.AfterIncomeUpdatedSameMonth.TOTAL_INCOME, testBudget.getTotalIncome());
        assertEquals(BudgetTestConstants.AfterIncomeUpdatedSameMonth.SAVINGS, testBudget.getSavings());
    }

    @Test
    void reapply_DifferentMonth_UpdatesBothBudgets() {
        Income updatedIncome = IncomeTestBuilder.updatedIncome()
            .withMonth(IncomeTestConstants.Updated.YEAR_MONTH)
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

        incomeBudgetSynchronizer.reapply(testIncome, updatedIncome);

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).findByMonth(IncomeTestConstants.Updated.YEAR_MONTH);
        verify(budgetRepository).save(newBudget);
        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalIncome());
        assertEquals(BudgetTestConstants.Default.TOTAL_EXPENSE.negate(), testBudget.getSavings());
        assertEquals(BudgetTestConstants.AfterIncomeMovedToDifferentMonth.TOTAL_INCOME, newBudget.getTotalIncome());
        assertEquals(BudgetTestConstants.AfterIncomeMovedToDifferentMonth.SAVINGS, newBudget.getSavings());
    }

    @Test
    void reapply_OriginalBudgetNotFound_ThrowsException() {
        Income updatedIncome = IncomeTestBuilder.defaultIncome()
            .withMonth(IncomeTestConstants.Updated.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(IncomeTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            incomeBudgetSynchronizer.reapply(testIncome, updatedIncome)
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

        incomeBudgetSynchronizer.retract(testIncome);

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalIncome());
        assertEquals(BudgetTestConstants.Default.TOTAL_EXPENSE.negate(), testBudget.getSavings());
    }

    @Test
    void retract_BudgetNotFound_ThrowsException() {
        when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            incomeBudgetSynchronizer.retract(testIncome)
        );

        verify(budgetRepository).findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository, never()).save(any(Budget.class));
    }
}
