package com.budgetmaster.application.service;

import java.util.Optional;

import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.util.DateUtils;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.budget.builder.BudgetBuilder;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.constants.Error;

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
		testBudget = BudgetBuilder.defaultBudget().build();
	}
	
	@Test
	void getBudget_ValidMonth_ReturnsOk() {
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(BudgetConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(BudgetConstants.Default.YEAR_MONTH);
			Mockito.when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
					.thenReturn(Optional.of(testBudget));

			Budget retrievedBudget = budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString());

			assertNotNull(retrievedBudget);
			assertEquals(BudgetConstants.Default.TOTAL_INCOME, retrievedBudget.getTotalIncome());
			assertEquals(BudgetConstants.Default.TOTAL_EXPENSE, retrievedBudget.getTotalExpense());
			assertEquals(BudgetConstants.Default.SAVINGS, retrievedBudget.getSavings());
			assertEquals(BudgetConstants.Default.CURRENCY, retrievedBudget.getCurrency());
			assertEquals(BudgetConstants.Default.YEAR_MONTH, retrievedBudget.getMonth());

			Mockito.verify(budgetRepository, Mockito.times(1))
					.findByMonth(BudgetConstants.Default.YEAR_MONTH);
		}
	}
	
	@Test
	void getBudget_ValidId_ReturnsOk() {
		Mockito.when(budgetRepository.findById(BudgetConstants.Default.ID))
				.thenReturn(Optional.of(testBudget));
		
		Budget retrievedBudget = budgetService.getBudgetById(BudgetConstants.Default.ID);
		
		assertNotNull(retrievedBudget);
		assertEquals(BudgetConstants.Default.TOTAL_INCOME, retrievedBudget.getTotalIncome());
		assertEquals(BudgetConstants.Default.TOTAL_EXPENSE, retrievedBudget.getTotalExpense());
		assertEquals(BudgetConstants.Default.SAVINGS, retrievedBudget.getSavings());
		assertEquals(BudgetConstants.Default.CURRENCY, retrievedBudget.getCurrency());
		assertEquals(BudgetConstants.Default.YEAR_MONTH, retrievedBudget.getMonth());
		
		Mockito.verify(budgetRepository, Mockito.times(1))
				.findById(BudgetConstants.Default.ID);
	}
	
	@Test
	void deleteBudget_ValidId_ReturnsNoContent() {
		Mockito.when(budgetRepository.findById(BudgetConstants.Default.ID))
				.thenReturn(Optional.of(testBudget));
		Mockito.doNothing()
				.when(budgetRepository)
				.deleteById(BudgetConstants.Default.ID);
		
		budgetService.deleteBudget(BudgetConstants.Default.ID);

		Mockito.verify(budgetRepository, Mockito.times(1))
				.findById(BudgetConstants.Default.ID);
		Mockito.verify(budgetRepository, Mockito.times(1))
				.deleteById(BudgetConstants.Default.ID);
	}
	
	@Test
	void getBudget_NonExistentMonth_ReturnsNotFound() {
		String errorMessage = String.format(Error.Budget.NOT_FOUND_FOR_MONTH, BudgetConstants.Default.YEAR_MONTH);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(BudgetConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(BudgetConstants.Default.YEAR_MONTH);
			Mockito.when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
					.thenReturn(Optional.empty());

			BudgetNotFoundException exception = assertThrows(
					BudgetNotFoundException.class,
					() -> budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString()),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}
	
	@Test
	void getBudget_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(Error.Budget.NOT_FOUND_WITH_ID, BudgetConstants.NonExistent.ID);
		Mockito.when(budgetRepository.findById(BudgetConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> budgetService.getBudgetById(BudgetConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void deleteBudget_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(Error.Budget.NOT_FOUND_WITH_ID, BudgetConstants.NonExistent.ID);
		Mockito.when(budgetRepository.findById(BudgetConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> budgetService.deleteBudget(BudgetConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(budgetRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}