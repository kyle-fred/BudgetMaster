package com.budgetmaster.budget.service.logic;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.Optional;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IncomeBudgetSynchronizerTest {
    // -- Dependencies --
    private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
    private final IncomeBudgetSynchronizer incomeBudgetSynchronizer = new IncomeBudgetSynchronizer(budgetRepository);

    // -- Test Data --
    private static final Long testId = TestData.CommonTestDataConstants.ID_EXISTING;
    private static final String testName = TestData.IncomeTestDataConstants.NAME;
    private static final String testSource = TestData.IncomeTestDataConstants.SOURCE;
    private static final BigDecimal testAmount = TestData.MoneyDtoTestDataConstants.AMOUNT;
    private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
    private static final TransactionType testType = TestData.IncomeTestDataConstants.TYPE_ONE_TIME;
    private static final YearMonth testMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
    private static final YearMonth testMonthUpdated = TestData.MonthTestDataConstants.MONTH_NON_EXISTING;

    // -- Test Objects --
    private Income testIncome;
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        // Setup Income
        testIncome = new Income();
        testIncome.setId(testId);
        testIncome.setName(testName);
        testIncome.setSource(testSource);
        testIncome.setMoney(Money.of(testAmount, testCurrency));
        testIncome.setType(testType);
        testIncome.setMonth(testMonth);

        // Setup Budget
        testBudget = Budget.of(testMonth, testCurrency);
        testBudget.setId(testId);
    }

    // -- Apply Tests --

    @Test
    void apply_ExistingBudget_UpdatesBudget() {
        when(budgetRepository.findByMonth(testMonth))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.apply(testIncome);

        verify(budgetRepository).findByMonth(testMonth);
        verify(budgetRepository).save(testBudget);
        assertEquals(testAmount, testBudget.getTotalIncome());
        assertEquals(testAmount, testBudget.getSavings());
    }

    @Test
    void apply_NoExistingBudget_CreatesNewBudget() {
        when(budgetRepository.findByMonth(testMonth))
            .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        incomeBudgetSynchronizer.apply(testIncome);

		ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
		verify(budgetRepository).save(captor.capture());
		Budget budget = captor.getValue();

        verify(budgetRepository).findByMonth(testMonth);
        assertEquals(testAmount, budget.getTotalIncome());
        assertEquals(testAmount, budget.getSavings());
    }

    // -- Reapply Tests --

    @Test
    void reapply_SameMonth_UpdatesBudget() {
        testBudget.addIncome(testIncome.getMoney().getAmount());
		
		Income updatedIncome = testIncome.deepCopy();
        updatedIncome.setMoney(Money.of(testAmount.multiply(BigDecimal.valueOf(2)), testCurrency));

        when(budgetRepository.findByMonth(testMonth))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.reapply(testIncome, updatedIncome);

		verify(budgetRepository, times(2)).findByMonth(testMonth);
        verify(budgetRepository).save(testBudget);
        assertEquals(testAmount.multiply(BigDecimal.valueOf(2)), testBudget.getTotalIncome());
        assertEquals(testAmount.multiply(BigDecimal.valueOf(2)), testBudget.getSavings());
    }

    @Test
    void reapply_DifferentMonth_UpdatesBothBudgets() {
        testBudget.addIncome(testIncome.getMoney().getAmount());
		
		Income updatedIncome = testIncome.deepCopy();
        updatedIncome.setMonth(testMonthUpdated);

        Budget newBudget = Budget.of(testMonthUpdated, testCurrency);
        newBudget.setId(testId + 1);

        when(budgetRepository.findByMonth(testMonth))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.findByMonth(testMonthUpdated))
            .thenReturn(Optional.of(newBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget)
            .thenReturn(newBudget);

        incomeBudgetSynchronizer.reapply(testIncome, updatedIncome);

        verify(budgetRepository).findByMonth(testMonth);
        verify(budgetRepository).findByMonth(testMonthUpdated);
        verify(budgetRepository).save(newBudget);
        assertEquals(0, testBudget.getTotalIncome().compareTo(BigDecimal.ZERO));
        assertEquals(0, testBudget.getSavings().compareTo(BigDecimal.ZERO));
        assertEquals(testAmount, newBudget.getTotalIncome());
        assertEquals(testAmount, newBudget.getSavings());
    }

    @Test
    void reapply_OriginalBudgetNotFound_ThrowsException() {
        Income updatedIncome = testIncome.deepCopy();
        updatedIncome.setMonth(testMonthUpdated);

        when(budgetRepository.findByMonth(testMonth))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            incomeBudgetSynchronizer.reapply(testIncome, updatedIncome)
        );

        verify(budgetRepository).findByMonth(testMonth);
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    // -- Retract Tests --

    @Test
    void retract_ExistingBudget_UpdatesBudget() {
        testBudget.setTotalIncome(testAmount);
        testBudget.setSavings(testAmount);

        when(budgetRepository.findByMonth(testMonth))
            .thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class)))
            .thenReturn(testBudget);

        incomeBudgetSynchronizer.retract(testIncome);

        verify(budgetRepository).findByMonth(testMonth);
        verify(budgetRepository).save(testBudget);
        assertEquals(0, testBudget.getTotalIncome().compareTo(BigDecimal.ZERO));
        assertEquals(0, testBudget.getSavings().compareTo(BigDecimal.ZERO));
    }

    @Test
    void retract_BudgetNotFound_ThrowsException() {
        when(budgetRepository.findByMonth(testMonth))
            .thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> 
            incomeBudgetSynchronizer.retract(testIncome)
        );

        verify(budgetRepository).findByMonth(testMonth);
        verify(budgetRepository, never()).save(any(Budget.class));
    }
}
