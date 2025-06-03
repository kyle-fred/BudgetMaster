package com.budgetmaster.application.service.synchronization;

import java.util.Optional;

import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.assertions.model.BudgetModelAssertions;
import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(JacksonConfig.class)
public class IncomeBudgetSynchronizerTest {

    private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
    private final IncomeBudgetSynchronizer incomeBudgetSynchronizer = new IncomeBudgetSynchronizer(budgetRepository);

    private Income testIncome;
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testIncome = IncomeBuilder.defaultIncome().build();
        testBudget = BudgetBuilder.defaultBudget().build();
    }

    @Test
    void apply_ExistingBudget_UpdatesBudget() {
        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.apply(testIncome);

        BudgetModelAssertions.assertBudget(testBudget)
            .hasTotalIncome(BudgetConstants.AfterAddIncome_WhenBudgetExists.TOTAL_INCOME)
            .hasSavings(BudgetConstants.AfterAddIncome_WhenBudgetExists.SAVINGS);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
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

        BudgetModelAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.AfterAddIncome_WhenNoBudgetExists.TOTAL_INCOME)
            .hasSavings(BudgetConstants.AfterAddIncome_WhenNoBudgetExists.TOTAL_INCOME);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
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

        BudgetModelAssertions.assertBudget(testBudget)
            .hasTotalIncome(BudgetConstants.AfterReapplyIncome_SameMonth.TOTAL_INCOME)
            .hasSavings(BudgetConstants.AfterReapplyIncome_SameMonth.SAVINGS);

		verify(budgetRepository, times(2)).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
    }

    @Test
    void reapply_DifferentMonth_UpdatesBothBudgets() {
        Income updatedIncome = IncomeBuilder.updatedIncome()
            .withMonth(IncomeConstants.Updated.YEAR_MONTH)
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

        incomeBudgetSynchronizer.reapply(testIncome, updatedIncome);

        BudgetModelAssertions.assertBudget(testBudget)
            .hasTotalIncome(BudgetConstants.AfterReapplyIncome_DifferentMonth.ExistingBudget.TOTAL_INCOME)
            .hasSavings(BudgetConstants.AfterReapplyIncome_DifferentMonth.ExistingBudget.SAVINGS);

        BudgetModelAssertions.assertBudget(newBudget)
            .hasTotalIncome(BudgetConstants.AfterReapplyIncome_DifferentMonth.NewBudget.TOTAL_INCOME)
            .hasSavings(BudgetConstants.AfterReapplyIncome_DifferentMonth.NewBudget.SAVINGS);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).findByMonth(BudgetConstants.Updated.YEAR_MONTH);
        verify(budgetRepository).save(newBudget);
    }

    @Test
    void reapply_OriginalBudgetNotFound_ThrowsException() {
        Income updatedIncome = IncomeBuilder.defaultIncome()
            .withMonth(IncomeConstants.Updated.YEAR_MONTH)
            .build();

        when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
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

        BudgetModelAssertions.assertBudget(testBudget)
            .hasTotalIncome(BudgetConstants.AfterRetractIncome.TOTAL_INCOME)
            .hasSavings(BudgetConstants.AfterRetractIncome.SAVINGS);

        verify(budgetRepository).findByMonth(BudgetConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
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
