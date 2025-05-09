package com.budgetmaster.budget.service.logic;

import java.math.BigDecimal;
import java.util.Optional;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.testsupport.budget.builder.BudgetBuilder;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.income.builder.IncomeBuilder;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
import com.budgetmaster.testsupport.income.factory.IncomeFactory;

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
        testIncome = IncomeFactory.createDefaultIncome();
        testBudget = BudgetBuilder.defaultBudget()
            .withTotalIncome(BudgetConstants.Default.TOTAL_INCOME)
            .withTotalExpense(BudgetConstants.Default.TOTAL_EXPENSE)
            .withSavings(BudgetConstants.Default.SAVINGS)
            .build();
    }

    @Test
    void apply_ExistingBudget_UpdatesBudget() {
        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.apply(testIncome);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BudgetConstants.AfterIncomeApplied.TOTAL_INCOME, testBudget.getTotalIncome());
        assertEquals(BudgetConstants.AfterIncomeApplied.SAVINGS, testBudget.getSavings());
    }

    @Test
    void apply_NoExistingBudget_CreatesNewBudget() {
        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        incomeBudgetSynchronizer.apply(testIncome);

		ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
		verify(budgetRepository).save(captor.capture());
		Budget budget = captor.getValue();

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        assertEquals(BudgetConstants.Default.TOTAL_INCOME, budget.getTotalIncome());
        assertEquals(BudgetConstants.Default.TOTAL_INCOME, budget.getSavings());
    }

    @Test
    void reapply_SameMonth_UpdatesBudget() {
        Income updatedIncome = IncomeBuilder.updatedIncome()
            .withMonth(IncomeConstants.Default.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.reapply(testIncome, updatedIncome);

		verify(budgetRepository, times(2)).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BudgetConstants.AfterIncomeUpdatedSameMonth.TOTAL_INCOME, testBudget.getTotalIncome());
        assertEquals(BudgetConstants.AfterIncomeUpdatedSameMonth.SAVINGS, testBudget.getSavings());
    }

    @Test
    void reapply_DifferentMonth_UpdatesBothBudgets() {
        Income updatedIncome = IncomeBuilder.updatedIncome()
            .withMonth(IncomeConstants.Updated.YEAR_MONTH)
            .build();

        Budget newBudget = BudgetBuilder.defaultBudget()
            .withId(testBudget.getId() + 1)
            .withTotalIncome(BigDecimal.ZERO)
            .withTotalExpense(BigDecimal.ZERO)
            .withSavings(BigDecimal.ZERO)
            .withMonth(BudgetConstants.Updated.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH))
            .thenReturn(Optional.of(newBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget)
            .thenReturn(newBudget);

        incomeBudgetSynchronizer.reapply(testIncome, updatedIncome);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).findByMonth(IncomeConstants.Updated.YEAR_MONTH);
        verify(budgetRepository).save(newBudget);
        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalIncome());
        assertEquals(BudgetConstants.Default.TOTAL_EXPENSE.negate(), testBudget.getSavings());
        assertEquals(BudgetConstants.AfterIncomeMovedToDifferentMonth.TOTAL_INCOME, newBudget.getTotalIncome());
        assertEquals(BudgetConstants.AfterIncomeMovedToDifferentMonth.SAVINGS, newBudget.getSavings());
    }

    @Test
    void reapply_OriginalBudgetNotFound_ThrowsException() {
        Income updatedIncome = IncomeBuilder.defaultIncome()
            .withMonth(IncomeConstants.Updated.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(IncomeConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            incomeBudgetSynchronizer.reapply(testIncome, updatedIncome)
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

        incomeBudgetSynchronizer.retract(testIncome);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(BigDecimal.ZERO.setScale(2), testBudget.getTotalIncome());
        assertEquals(BudgetConstants.Default.TOTAL_EXPENSE.negate(), testBudget.getSavings());
    }

    @Test
    void retract_BudgetNotFound_ThrowsException() {
        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            incomeBudgetSynchronizer.retract(testIncome)
        );

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository, never()).save(any(Budget.class));
    }
}
