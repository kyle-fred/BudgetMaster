package com.budgetmaster.service;

import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.model.Budget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

class BudgetServiceTest {
	// -- Dependencies --
	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);
	
	// -- Test Data --
	private static final Long testId = 1L;
	private static final BigDecimal testTotalIncome = new BigDecimal("543.21");
	private static final BigDecimal testTotalExpense = new BigDecimal("123.45");
	private static final BigDecimal testSavings = testTotalIncome.subtract(testTotalExpense);
	private static final String testMonth = "2000-01";
	private static final YearMonth testYearMonth = YearMonth.of(2000, 1);
	
	// -- Test Objects --
	private Budget testBudget;
	
	// -- Setup --
	
	@BeforeEach
	void setUp() {
		// Setup Budget
		testBudget = new Budget(testYearMonth);
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

			assertNotNull(retrievedBudget, "Budget should not be null");
			assertEquals(testTotalIncome, retrievedBudget.getTotalIncome(), "Total income should be equal to the test value");
			assertEquals(testTotalExpense, retrievedBudget.getTotalExpense(), "Total expense should be equal to the test value");
			assertEquals(testSavings, retrievedBudget.getSavings(), "Savings should be equal to the test value");
			assertEquals(testYearMonth, retrievedBudget.getMonth(), "Month should be equal to the test value");

			Mockito.verify(budgetRepository, Mockito.times(1))
					.findByMonth(testYearMonth);
		}
	}
	
	@Test
	void getBudget_ValidId_ReturnsOk() {
		Mockito.when(budgetRepository.findById(testId))
				.thenReturn(Optional.of(testBudget));
		
		Budget retrievedBudget = budgetService.getBudgetById(testId);
		
		assertNotNull(retrievedBudget, "Budget should not be null");
		assertEquals(testTotalIncome, retrievedBudget.getTotalIncome(), "Total income should be equal to the test value");
		assertEquals(testTotalExpense, retrievedBudget.getTotalExpense(), "Total expense should be equal to the test value");
		assertEquals(testSavings, retrievedBudget.getSavings(), "Savings should be equal to the test value");
		assertEquals(testYearMonth, retrievedBudget.getMonth(), "Month should be equal to the test value");
		
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
		String errorMessage = "Budget not found for month: " + testYearMonth;
		
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
			
			assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
		}
	}
	
	@Test
	void getBudget_NonExistentId_ReturnsNotFound() {
		String errorMessage = "Budget not found for id: 99";
		Mockito.when(budgetRepository.findById(99L))
				.thenReturn(Optional.empty());
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> budgetService.getBudgetById(99L),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
	}
	
	@Test
	void deleteBudget_NonExistentId_ReturnsNotFound() {
		String errorMessage = "Budget not found for id: 99";
		Mockito.when(budgetRepository.findById(99L))
				.thenReturn(Optional.empty());
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> budgetService.deleteBudget(99L),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
		Mockito.verify(budgetRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}