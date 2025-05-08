package com.budgetmaster.budget.service.logic;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.Optional;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.builder.IncomeTestBuilder;
import com.budgetmaster.test.constants.IncomeTestConstants;
import com.budgetmaster.test.constants.TestData;
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

    // -- Test Data --
    private static final Long testId = TestData.CommonTestDataConstants.ID_EXISTING;
    private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
    private static final YearMonth testMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;

    // -- Test Objects --
    private Income testIncome;
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testIncome = IncomeTestFactory.createDefaultIncome();
        testBudget = Budget.of(testMonth, testCurrency);
        testBudget.setId(testId);
    }

    @Test
    void apply_ExistingBudget_UpdatesBudget() {
        when(budgetRepository.findByMonth(IncomeTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.apply(testIncome);

        verify(budgetRepository).findByMonth(IncomeTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(IncomeTestConstants.Default.AMOUNT, testBudget.getTotalIncome());
        assertEquals(IncomeTestConstants.Default.AMOUNT, testBudget.getSavings());
    }

    @Test
    void apply_NoExistingBudget_CreatesNewBudget() {
        when(budgetRepository.findByMonth(IncomeTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        incomeBudgetSynchronizer.apply(testIncome);

		ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
		verify(budgetRepository).save(captor.capture());
		Budget budget = captor.getValue();

        verify(budgetRepository).findByMonth(IncomeTestConstants.Default.YEAR_MONTH);
        assertEquals(IncomeTestConstants.Default.AMOUNT, budget.getTotalIncome());
        assertEquals(IncomeTestConstants.Default.AMOUNT, budget.getSavings());
    }

    @Test
    void reapply_SameMonth_UpdatesBudget() {
        testBudget.addIncome(testIncome.getMoney().getAmount());
		
		Income updatedIncome = testIncome.deepCopy();
        updatedIncome.setMoney(Money.of(IncomeTestConstants.Updated.AMOUNT, IncomeTestConstants.Default.CURRENCY));

        when(budgetRepository.findByMonth(IncomeTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.reapply(testIncome, updatedIncome);

		verify(budgetRepository, times(2)).findByMonth(IncomeTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(IncomeTestConstants.Updated.AMOUNT, testBudget.getTotalIncome());
        assertEquals(IncomeTestConstants.Updated.AMOUNT, testBudget.getSavings());
    }

    @Test
    void reapply_DifferentMonth_UpdatesBothBudgets() {
        testBudget.addIncome(testIncome.getMoney().getAmount());

        Income updatedIncome = IncomeTestBuilder.defaultIncome()
            .withMonth(IncomeTestConstants.Updated.YEAR_MONTH)
            .build();

        Budget newBudget = Budget.of(IncomeTestConstants.Updated.YEAR_MONTH, IncomeTestConstants.Default.CURRENCY);
        newBudget.setId(testId + 1);

        when(budgetRepository.findByMonth(IncomeTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.findByMonth(IncomeTestConstants.Updated.YEAR_MONTH))
            .thenReturn(Optional.of(newBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget)
            .thenReturn(newBudget);

        incomeBudgetSynchronizer.reapply(testIncome, updatedIncome);

        verify(budgetRepository).findByMonth(IncomeTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).findByMonth(IncomeTestConstants.Updated.YEAR_MONTH);
        verify(budgetRepository).save(newBudget);
        assertEquals(0, testBudget.getTotalIncome().compareTo(BigDecimal.ZERO));
        assertEquals(0, testBudget.getSavings().compareTo(BigDecimal.ZERO));
        assertEquals(IncomeTestConstants.Default.AMOUNT, newBudget.getTotalIncome());
        assertEquals(IncomeTestConstants.Default.AMOUNT, newBudget.getSavings());
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

        verify(budgetRepository).findByMonth(IncomeTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void retract_ExistingBudget_UpdatesBudget() {
        testBudget.setTotalIncome(IncomeTestConstants.Default.AMOUNT);
        testBudget.setSavings(IncomeTestConstants.Default.AMOUNT);

        when(budgetRepository.findByMonth(IncomeTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.retract(testIncome);

        verify(budgetRepository).findByMonth(IncomeTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository).save(testBudget);
        assertEquals(0, testBudget.getTotalIncome().compareTo(BigDecimal.ZERO));
        assertEquals(0, testBudget.getSavings().compareTo(BigDecimal.ZERO));
    }

    @Test
    void retract_BudgetNotFound_ThrowsException() {
        when(budgetRepository.findByMonth(IncomeTestConstants.Default.YEAR_MONTH))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            incomeBudgetSynchronizer.retract(testIncome)
        );

        verify(budgetRepository).findByMonth(IncomeTestConstants.Default.YEAR_MONTH);
        verify(budgetRepository, never()).save(any(Budget.class));
    }
}
