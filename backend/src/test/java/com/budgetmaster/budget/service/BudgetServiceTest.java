package com.budgetmaster.budget.service;

import java.util.Optional;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.test.builder.BudgetTestBuilder;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestData.BudgetTestConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@Import(JacksonConfig.class)
public class BudgetServiceTest {
	// -- Dependencies --
	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);

	// -- Test Objects --
	private Budget testBudget;
	
	@BeforeEach
	void setUp() {
		testBudget = BudgetTestBuilder.defaultBudget()
			.withTotalIncome(BudgetTestConstants.Default.TOTAL_INCOME)
			.withTotalExpense(BudgetTestConstants.Default.TOTAL_EXPENSE)
			.withSavings(BudgetTestConstants.Default.SAVINGS)
			.build();
	}
	
	@Test
	void getBudget_ValidMonth_ReturnsOk() {
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(BudgetTestConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(BudgetTestConstants.Default.YEAR_MONTH);
			Mockito.when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
					.thenReturn(Optional.of(testBudget));

			Budget retrievedBudget = budgetService.getBudgetByMonth(BudgetTestConstants.Default.YEAR_MONTH.toString());

			assertNotNull(retrievedBudget);
			assertEquals(BudgetTestConstants.Default.TOTAL_INCOME, retrievedBudget.getTotalIncome());
			assertEquals(BudgetTestConstants.Default.TOTAL_EXPENSE, retrievedBudget.getTotalExpense());
			assertEquals(BudgetTestConstants.Default.SAVINGS, retrievedBudget.getSavings());
			assertEquals(BudgetTestConstants.Default.CURRENCY, retrievedBudget.getCurrency());
			assertEquals(BudgetTestConstants.Default.YEAR_MONTH, retrievedBudget.getMonth());

			Mockito.verify(budgetRepository, Mockito.times(1))
					.findByMonth(BudgetTestConstants.Default.YEAR_MONTH);
		}
	}
	
	@Test
	void getBudget_ValidId_ReturnsOk() {
		Mockito.when(budgetRepository.findById(BudgetTestConstants.Default.ID))
				.thenReturn(Optional.of(testBudget));
		
		Budget retrievedBudget = budgetService.getBudgetById(BudgetTestConstants.Default.ID);
		
		assertNotNull(retrievedBudget);
		assertEquals(BudgetTestConstants.Default.TOTAL_INCOME, retrievedBudget.getTotalIncome());
		assertEquals(BudgetTestConstants.Default.TOTAL_EXPENSE, retrievedBudget.getTotalExpense());
		assertEquals(BudgetTestConstants.Default.SAVINGS, retrievedBudget.getSavings());
		assertEquals(BudgetTestConstants.Default.CURRENCY, retrievedBudget.getCurrency());
		assertEquals(BudgetTestConstants.Default.YEAR_MONTH, retrievedBudget.getMonth());
		
		Mockito.verify(budgetRepository, Mockito.times(1))
				.findById(BudgetTestConstants.Default.ID);
	}
	
	@Test
	void deleteBudget_ValidId_ReturnsNoContent() {
		Mockito.when(budgetRepository.findById(BudgetTestConstants.Default.ID))
				.thenReturn(Optional.of(testBudget));
		Mockito.doNothing()
				.when(budgetRepository)
				.deleteById(BudgetTestConstants.Default.ID);
		
		budgetService.deleteBudget(BudgetTestConstants.Default.ID);

		Mockito.verify(budgetRepository, Mockito.times(1))
				.findById(BudgetTestConstants.Default.ID);
		Mockito.verify(budgetRepository, Mockito.times(1))
				.deleteById(BudgetTestConstants.Default.ID);
	}
	
	@Test
	void getBudget_NonExistentMonth_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, BudgetTestConstants.Default.YEAR_MONTH);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(BudgetTestConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(BudgetTestConstants.Default.YEAR_MONTH);
			Mockito.when(budgetRepository.findByMonth(BudgetTestConstants.Default.YEAR_MONTH))
					.thenReturn(Optional.empty());

			BudgetNotFoundException exception = assertThrows(
					BudgetNotFoundException.class,
					() -> budgetService.getBudgetByMonth(BudgetTestConstants.Default.YEAR_MONTH.toString()),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}
	
	@Test
	void getBudget_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, BudgetTestConstants.NonExistent.ID);
		Mockito.when(budgetRepository.findById(BudgetTestConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> budgetService.getBudgetById(BudgetTestConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void deleteBudget_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, BudgetTestConstants.NonExistent.ID);
		Mockito.when(budgetRepository.findById(BudgetTestConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> budgetService.deleteBudget(BudgetTestConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(budgetRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}