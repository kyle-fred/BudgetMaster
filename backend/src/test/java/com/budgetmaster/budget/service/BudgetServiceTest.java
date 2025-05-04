package com.budgetmaster.budget.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.Optional;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.test.constants.TestData;
import com.budgetmaster.test.constants.TestMessages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class BudgetServiceTest {
	// -- Dependencies --
	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);
	
	// -- Test Data --
	private static final Long testId = TestData.CommonTestDataConstants.ID_EXISTING;
	private static final Long testIdNonExistent = TestData.CommonTestDataConstants.ID_NON_EXISTING;
	private static final BigDecimal testTotalIncome = TestData.BudgetTestDataConstants.INCOME_AMOUNT;
	private static final BigDecimal testTotalExpense = TestData.BudgetTestDataConstants.EXPENSE_AMOUNT;
	private static final BigDecimal testSavings = TestData.BudgetTestDataConstants.SAVINGS_AMOUNT;
	private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
	private static final String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;
	private static final YearMonth testYearMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
	
	// -- Test Objects --
	private Budget testBudget;
	
	// -- Setup --
	
	@BeforeEach
	void setUp() {
		// Setup Budget
		testBudget = Budget.of(testYearMonth, testCurrency);
		testBudget.setId(testId);
		testBudget.setTotalIncome(testTotalIncome);
		testBudget.setTotalExpense(testTotalExpense);
		testBudget.setSavings(testSavings);
	}
	
	// -- Get Budget Tests --
	
	@Test
	void getBudget_ValidMonth_ReturnsOk() {
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(testMonth))
					.thenReturn(testYearMonth);
			Mockito.when(budgetRepository.findByMonth(testYearMonth))
					.thenReturn(Optional.of(testBudget));

			Budget retrievedBudget = budgetService.getBudgetByMonth(testMonth);

			assertNotNull(retrievedBudget);
			assertEquals(testTotalIncome, retrievedBudget.getTotalIncome());
			assertEquals(testTotalExpense, retrievedBudget.getTotalExpense());
			assertEquals(testSavings, retrievedBudget.getSavings());
			assertEquals(testCurrency, retrievedBudget.getCurrency());
			assertEquals(testYearMonth, retrievedBudget.getMonth());

			Mockito.verify(budgetRepository, Mockito.times(1))
					.findByMonth(testYearMonth);
		}
	}
	
	@Test
	void getBudget_ValidId_ReturnsOk() {
		Mockito.when(budgetRepository.findById(testId))
				.thenReturn(Optional.of(testBudget));
		
		Budget retrievedBudget = budgetService.getBudgetById(testId);
		
		assertNotNull(retrievedBudget);
		assertEquals(testTotalIncome, retrievedBudget.getTotalIncome());
		assertEquals(testTotalExpense, retrievedBudget.getTotalExpense());
		assertEquals(testSavings, retrievedBudget.getSavings());
		assertEquals(testCurrency, retrievedBudget.getCurrency());
		assertEquals(testYearMonth, retrievedBudget.getMonth());
		
		Mockito.verify(budgetRepository, Mockito.times(1))
				.findById(testId);
	}
	
	// -- Delete Budget Tests --
	
	@Test
	void deleteBudget_ValidId_ReturnsNoContent() {
		Mockito.when(budgetRepository.findById(testId))
				.thenReturn(Optional.of(testBudget));
		Mockito.doNothing()
				.when(budgetRepository)
				.deleteById(testId);
		
		budgetService.deleteBudget(testId);

		Mockito.verify(budgetRepository, Mockito.times(1))
				.findById(testId);
		Mockito.verify(budgetRepository, Mockito.times(1))
				.deleteById(testId);
	}
	
	// -- Error Handling Tests --
	
	@Test
	void getBudget_NonExistentMonth_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, testYearMonth);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(testMonth))
					.thenReturn(testYearMonth);
			Mockito.when(budgetRepository.findByMonth(testYearMonth))
					.thenReturn(Optional.empty());

			BudgetNotFoundException exception = assertThrows(
					BudgetNotFoundException.class,
					() -> budgetService.getBudgetByMonth(testMonth),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}
	
	@Test
	void getBudget_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, testIdNonExistent);
		Mockito.when(budgetRepository.findById(testId))
				.thenReturn(Optional.empty());
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> budgetService.getBudgetById(testIdNonExistent),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void deleteBudget_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, testIdNonExistent);
		Mockito.when(budgetRepository.findById(testIdNonExistent))
				.thenReturn(Optional.empty());
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> budgetService.deleteBudget(testIdNonExistent),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(budgetRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}