package com.budgetmaster.application.service;

import java.util.Optional;

import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.util.DateUtils;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.assertions.model.BudgetModelAssertions;
import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(JacksonConfig.class)
@DisplayName("Budget Service Tests")
class BudgetServiceTest {

	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);

	private Budget defaultBudget;
	
	@BeforeEach
	void setUp() {
		defaultBudget = BudgetBuilder.defaultBudget().build();
	}

	@Nested
	@DisplayName("Get Budget Operations")
	class GetBudgetOperations {
		
		@Test
		@DisplayName("Should return budget when found by month")
		void getBudgetByMonth_withValidMonth_returnsBudget() {
			try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
				mockedDateUtils.when(() -> DateUtils.getValidYearMonth(BudgetConstants.Default.YEAR_MONTH.toString()))
						.thenReturn(BudgetConstants.Default.YEAR_MONTH);
				when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
						.thenReturn(Optional.of(defaultBudget));

				Budget retrievedBudget = budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString());

				BudgetModelAssertions.assertBudget(retrievedBudget)
					.isDefaultBudget();

				verify(budgetRepository, times(1))
						.findByMonth(BudgetConstants.Default.YEAR_MONTH);
			}
		}

		@Test
		@DisplayName("Should return budget when found by ID")
		void getBudgetById_withValidId_returnsBudget() {
			when(budgetRepository.findById(BudgetConstants.Default.ID))
					.thenReturn(Optional.of(defaultBudget));
			
			Budget retrievedBudget = budgetService.getBudgetById(BudgetConstants.Default.ID);
			
			BudgetModelAssertions.assertBudget(retrievedBudget)
				.isDefaultBudget();
			
			verify(budgetRepository, times(1))
					.findById(BudgetConstants.Default.ID);
		}

		@Test
		@DisplayName("Should throw exception when budget not found by month")
		void getBudgetByMonth_withNonExistentMonth_throwsException() {
			String errorMessage = String.format(ErrorConstants.Budget.NOT_FOUND_FOR_MONTH, BudgetConstants.Default.YEAR_MONTH);
			
			try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
				mockedDateUtils.when(() -> DateUtils.getValidYearMonth(BudgetConstants.Default.YEAR_MONTH.toString()))
						.thenReturn(BudgetConstants.Default.YEAR_MONTH);
				when(budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH))
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
		@DisplayName("Should throw exception when budget not found by ID")
		void getBudgetById_withNonExistentId_throwsException() {
			String errorMessage = String.format(ErrorConstants.Budget.NOT_FOUND_WITH_ID, BudgetConstants.NonExistent.ID);
			when(budgetRepository.findById(BudgetConstants.NonExistent.ID))
					.thenReturn(Optional.empty());
			
			BudgetNotFoundException exception = assertThrows(
					BudgetNotFoundException.class,
					() -> budgetService.getBudgetById(BudgetConstants.NonExistent.ID),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}

	@Nested
	@DisplayName("Delete Budget Operations")
	class DeleteBudgetOperations {
		
		@Test
		@DisplayName("Should delete budget when found by ID")
		void deleteBudget_withValidId_deletesBudget() {
			when(budgetRepository.findById(BudgetConstants.Default.ID))
					.thenReturn(Optional.of(defaultBudget));
			doNothing()
					.when(budgetRepository)
					.deleteById(BudgetConstants.Default.ID);
			
			budgetService.deleteBudget(BudgetConstants.Default.ID);

			verify(budgetRepository, times(1))
					.findById(BudgetConstants.Default.ID);
			verify(budgetRepository, times(1))
					.deleteById(BudgetConstants.Default.ID);
		}

		@Test
		@DisplayName("Should throw exception when budget not found during delete")
		void deleteBudget_withNonExistentId_throwsException() {
			String errorMessage = String.format(ErrorConstants.Budget.NOT_FOUND_WITH_ID, BudgetConstants.NonExistent.ID);
			when(budgetRepository.findById(BudgetConstants.NonExistent.ID))
					.thenReturn(Optional.empty());
			
			BudgetNotFoundException exception = assertThrows(
					BudgetNotFoundException.class,
					() -> budgetService.deleteBudget(BudgetConstants.NonExistent.ID),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage());
			
			verify(budgetRepository, never())
					.deleteById(anyLong());
		}
	}
}