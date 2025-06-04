package com.budgetmaster.application.service;

import java.util.List;
import java.util.Optional;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.application.service.synchronization.IncomeBudgetSynchronizer;
import com.budgetmaster.application.util.DateUtils;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.assertions.model.IncomeModelAssertions;
import com.budgetmaster.testsupport.assertions.model.list.IncomeListAssertions;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(JacksonConfig.class)
@DisplayName("Income Service Tests")
class IncomeServiceTest {

	private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
	private final IncomeBudgetSynchronizer incomeBudgetSynchronizer = mock(IncomeBudgetSynchronizer.class);
	private final IncomeService incomeService = new IncomeService(incomeRepository, incomeBudgetSynchronizer);
	
	private Income testIncome;
	private IncomeRequest incomeRequest;
	
	@BeforeEach
	void setUp() {
		testIncome = IncomeBuilder.defaultIncome().build();
		incomeRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();
	}

	@Nested
	@DisplayName("Create Income Operations")
	class CreateIncomeOperations {
		
		@Test
		@DisplayName("Should create income when request is valid")
		void createIncome_withValidRequest_returnsCreated() {
			when(incomeRepository.saveAndFlush(any(Income.class)))
					.thenReturn(testIncome);
			doNothing().when(incomeBudgetSynchronizer)
					.apply(any(Income.class));
			
			Income savedIncome = incomeService.createIncome(incomeRequest);

			IncomeModelAssertions.assertIncome(savedIncome)
				.isDefaultIncome();

			verify(incomeBudgetSynchronizer).apply(testIncome);
			verify(incomeRepository).saveAndFlush(any(Income.class));
		}

		@Test
		@DisplayName("Should throw exception when database error occurs during creation")
		void createIncome_withServiceError_throwsException() {
			String errorMessage = ErrorCode.DATABASE_ERROR.getMessage();
			
			when(incomeRepository.saveAndFlush(any(Income.class)))
					.thenThrow(new DataIntegrityViolationException(errorMessage));
			
			DataIntegrityViolationException exception = assertThrows(
					DataIntegrityViolationException.class,
					() -> incomeService.createIncome(incomeRequest)
			);
			
			assertEquals(errorMessage, exception.getMessage());

			verify(incomeBudgetSynchronizer, never()).apply(any(Income.class));
		}
	}

	@Nested
	@DisplayName("Get Income Operations")
	class GetIncomeOperations {
		
		@Test
		@DisplayName("Should return all incomes for valid month")
		void getAllIncomesForMonth_withValidMonth_returnsIncomes() {
			List<Income> incomes = List.of(testIncome, testIncome);
			
			try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
				mockedDateUtils.when(() -> DateUtils.getValidYearMonth(IncomeConstants.Default.YEAR_MONTH.toString()))
						.thenReturn(IncomeConstants.Default.YEAR_MONTH);
				when(incomeRepository.findByMonth(IncomeConstants.Default.YEAR_MONTH))
						.thenReturn(incomes);

				List<Income> result = incomeService.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH.toString());

				IncomeListAssertions.assertIncomes(result)
					.hasSize(2)
					.first()
					.isDefaultIncome();

				verify(incomeRepository).findByMonth(IncomeConstants.Default.YEAR_MONTH);
			}
		}

		@Test
		@DisplayName("Should return income when found by ID")
		void getIncomeById_withValidId_returnsIncome() {
			when(incomeRepository.findById(IncomeConstants.Default.ID))
					.thenReturn(Optional.of(testIncome));
			
			Income retrievedIncome = incomeService.getIncomeById(IncomeConstants.Default.ID);
			
			IncomeModelAssertions.assertIncome(retrievedIncome)
				.isDefaultIncome();
			
			verify(incomeRepository).findById(IncomeConstants.Default.ID);
		}

		@Test
		@DisplayName("Should throw exception when no incomes found for month")
		void getAllIncomesForMonth_withNoIncomes_throwsException() {
			String errorMessage = String.format(ErrorConstants.Income.NOT_FOUND_BY_MONTH, IncomeConstants.NonExistent.YEAR_MONTH);
			
			try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
				mockedDateUtils.when(() -> DateUtils.getValidYearMonth(IncomeConstants.NonExistent.YEAR_MONTH.toString()))
						.thenReturn(IncomeConstants.NonExistent.YEAR_MONTH);
				when(incomeRepository.findByMonth(IncomeConstants.NonExistent.YEAR_MONTH))
						.thenThrow(new IncomeNotFoundException(errorMessage));

				IncomeNotFoundException exception = assertThrows(
						IncomeNotFoundException.class,
						() -> incomeService.getAllIncomesForMonth(IncomeConstants.NonExistent.YEAR_MONTH.toString())
				);
				
				assertEquals(errorMessage, exception.getMessage());
			}
		}

		@Test
		@DisplayName("Should throw exception when income not found by ID")
		void getIncomeById_withNonExistentId_throwsException() {
			String errorMessage = String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID);
			
			when(incomeRepository.findById(IncomeConstants.NonExistent.ID))
					.thenReturn(Optional.empty());
			
			IncomeNotFoundException exception = assertThrows(
					IncomeNotFoundException.class,
					() -> incomeService.getIncomeById(IncomeConstants.NonExistent.ID)
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}

	@Nested
	@DisplayName("Update Income Operations")
	class UpdateIncomeOperations {
		
		@Test
		@DisplayName("Should update income when request is valid")
		void updateIncome_withValidRequest_returnsUpdated() {
			IncomeRequest updatedRequest = IncomeRequestBuilder.updatedIncomeRequest().buildRequest();

			when(incomeRepository.findById(IncomeConstants.Default.ID))
					.thenReturn(Optional.of(testIncome));
			doNothing().when(incomeBudgetSynchronizer)
					.reapply(any(Income.class), any(Income.class));
			when(incomeRepository.saveAndFlush(any(Income.class)))
					.thenReturn(testIncome);

			Income updatedIncome = incomeService.updateIncome(IncomeConstants.Default.ID, updatedRequest);

			IncomeModelAssertions.assertIncome(updatedIncome)
				.isUpdatedIncome();
			
			verify(incomeBudgetSynchronizer).reapply(any(Income.class), any(Income.class));
			verify(incomeRepository).saveAndFlush(any(Income.class));
		}

		@Test
		@DisplayName("Should throw exception when income not found during update")
		void updateIncome_withNonExistentId_throwsException() {
			String errorMessage = String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID);
			
			when(incomeRepository.findById(IncomeConstants.NonExistent.ID))
					.thenReturn(Optional.empty());

			IncomeNotFoundException exception = assertThrows(
					IncomeNotFoundException.class,
					() -> incomeService.updateIncome(IncomeConstants.NonExistent.ID, incomeRequest)
			);
			
			assertEquals(errorMessage, exception.getMessage());

			verify(incomeBudgetSynchronizer, never()).reapply(any(Income.class), any(Income.class));
			verify(incomeRepository, never()).saveAndFlush(any(Income.class));
		}
	}

	@Nested
	@DisplayName("Delete Income Operations")
	class DeleteIncomeOperations {
		
		@Test
		@DisplayName("Should delete income when found by ID")
		void deleteIncome_withValidId_deletesIncome() {
			when(incomeRepository.findById(IncomeConstants.Default.ID))
					.thenReturn(Optional.of(testIncome));
			doNothing().when(incomeBudgetSynchronizer)
					.retract(any(Income.class));
			doNothing()
					.when(incomeRepository)
					.deleteById(IncomeConstants.Default.ID);
			
			incomeService.deleteIncome(IncomeConstants.Default.ID);

			verify(incomeBudgetSynchronizer).retract(any(Income.class));
			verify(incomeRepository).findById(IncomeConstants.Default.ID);
			verify(incomeRepository).deleteById(IncomeConstants.Default.ID);
		}

		@Test
		@DisplayName("Should throw exception when income not found during delete")
		void deleteIncome_withNonExistentId_throwsException() {
			String errorMessage = String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID);
			
			when(incomeRepository.findById(IncomeConstants.NonExistent.ID))
					.thenReturn(Optional.empty());
			
			IncomeNotFoundException exception = assertThrows(
					IncomeNotFoundException.class,
					() -> incomeService.deleteIncome(IncomeConstants.NonExistent.ID)
			);
			
			assertEquals(errorMessage, exception.getMessage());
			
			verify(incomeBudgetSynchronizer, never()).retract(any(Income.class));
			verify(incomeRepository, never()).deleteById(anyLong());
		}
	}
}