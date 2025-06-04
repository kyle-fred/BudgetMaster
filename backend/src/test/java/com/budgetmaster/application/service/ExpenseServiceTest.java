package com.budgetmaster.application.service;

import java.util.List;
import java.util.Optional;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.application.exception.ExpenseNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.repository.ExpenseRepository;
import com.budgetmaster.application.service.synchronization.ExpenseBudgetSynchronizer;
import com.budgetmaster.application.util.DateUtils;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.assertions.model.ExpenseModelAssertions;
import com.budgetmaster.testsupport.assertions.model.list.ExpenseListAssertions;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

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
@DisplayName("Expense Service Tests")
class ExpenseServiceTest {

	private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
	private final ExpenseBudgetSynchronizer expenseBudgetSynchronizer = mock(ExpenseBudgetSynchronizer.class);
	private final ExpenseService expenseService = new ExpenseService(expenseRepository, expenseBudgetSynchronizer);
	
	private Expense testExpense;
	private ExpenseRequest expenseRequest;
	
	@BeforeEach
	void setUp() {
		testExpense = ExpenseBuilder.defaultExpense().build();
		expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();
	}

	@Nested
	@DisplayName("Create Expense Operations")
	class CreateExpenseOperations {
		
		@Test
		@DisplayName("Should create expense when request is valid")
		void createExpense_withValidRequest_returnsCreated() {
			when(expenseRepository.saveAndFlush(any(Expense.class)))
					.thenReturn(testExpense);
			doNothing().when(expenseBudgetSynchronizer)
					.apply(any(Expense.class));
			
			Expense savedExpense = expenseService.createExpense(expenseRequest);

			ExpenseModelAssertions.assertExpense(savedExpense)
				.isDefaultExpense();

			verify(expenseBudgetSynchronizer).apply(any(Expense.class));
			verify(expenseRepository).saveAndFlush(any(Expense.class));
		}

		@Test
		@DisplayName("Should throw exception when database error occurs during creation")
		void createExpense_withServiceError_throwsException() {
			String errorMessage = ErrorCode.DATABASE_ERROR.getMessage();

			when(expenseRepository.saveAndFlush(any(Expense.class)))
					.thenThrow(new DataIntegrityViolationException(errorMessage));
			
			DataIntegrityViolationException exception = assertThrows(
					DataIntegrityViolationException.class,
					() -> expenseService.createExpense(expenseRequest)
			);
			
			assertEquals(errorMessage, exception.getMessage());

			verify(expenseBudgetSynchronizer, never()).apply(any(Expense.class));
		}
	}

	@Nested
	@DisplayName("Get Expense Operations")
	class GetExpenseOperations {
		
		@Test
		@DisplayName("Should return all expenses for valid month")
		void getAllExpensesForMonth_withValidMonth_returnsExpenses() {
			List<Expense> expenses = List.of(testExpense, testExpense);
			
			try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
				mockedDateUtils.when(() -> DateUtils.getValidYearMonth(ExpenseConstants.Default.YEAR_MONTH.toString()))
						.thenReturn(ExpenseConstants.Default.YEAR_MONTH);
				when(expenseRepository.findByMonth(ExpenseConstants.Default.YEAR_MONTH))
						.thenReturn(expenses);

				List<Expense> result = expenseService.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString());

				ExpenseListAssertions.assertExpenses(result)
					.hasSize(2)
					.first()
					.isDefaultExpense();

				verify(expenseRepository).findByMonth(ExpenseConstants.Default.YEAR_MONTH);
			}
		}

		@Test
		@DisplayName("Should return expense when found by ID")
		void getExpenseById_withValidId_returnsExpense() {
			when(expenseRepository.findById(ExpenseConstants.Default.ID))
					.thenReturn(Optional.of(testExpense));
			
			Expense retrievedExpense = expenseService.getExpenseById(ExpenseConstants.Default.ID);
			
			ExpenseModelAssertions.assertExpense(retrievedExpense)
				.isDefaultExpense();
			
			verify(expenseRepository).findById(ExpenseConstants.Default.ID);
		}

		@Test
		@DisplayName("Should throw exception when no expenses found for month")
		void getAllExpensesForMonth_withNoExpenses_throwsException() {
			String errorMessage = String.format(ErrorConstants.Expense.NOT_FOUND_BY_MONTH, ExpenseConstants.Default.YEAR_MONTH.toString());
			
			try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
				mockedDateUtils.when(() -> DateUtils.getValidYearMonth(ExpenseConstants.Default.YEAR_MONTH.toString()))
						.thenReturn(ExpenseConstants.Default.YEAR_MONTH);
				when(expenseRepository.findByMonth(ExpenseConstants.Default.YEAR_MONTH))
						.thenThrow(new ExpenseNotFoundException(errorMessage));

				ExpenseNotFoundException exception = assertThrows(
						ExpenseNotFoundException.class,
						() -> expenseService.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString())
				);
				
				assertEquals(errorMessage, exception.getMessage());
			}
		}

		@Test
		@DisplayName("Should throw exception when expense not found by ID")
		void getExpenseById_withNonExistentId_throwsException() {
			String errorMessage = String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID);
			
			when(expenseRepository.findById(ExpenseConstants.NonExistent.ID))
					.thenReturn(Optional.empty());
			
			ExpenseNotFoundException exception = assertThrows(
					ExpenseNotFoundException.class,
					() -> expenseService.getExpenseById(ExpenseConstants.NonExistent.ID)
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}

	@Nested
	@DisplayName("Update Expense Operations")
	class UpdateExpenseOperations {
		
		@Test
		@DisplayName("Should update expense when request is valid")
		void updateExpense_withValidRequest_returnsUpdated() {
			ExpenseRequest updatedExpenseRequest = ExpenseRequestBuilder.updatedExpenseRequest().buildRequest();

			when(expenseRepository.findById(ExpenseConstants.Default.ID))
					.thenReturn(Optional.of(testExpense));
			doNothing().when(expenseBudgetSynchronizer)
					.reapply(any(Expense.class), any(Expense.class));
			when(expenseRepository.saveAndFlush(any(Expense.class)))
					.thenReturn(testExpense);

			Expense updatedExpense = expenseService.updateExpense(ExpenseConstants.Default.ID, updatedExpenseRequest);

			ExpenseModelAssertions.assertExpense(updatedExpense)
				.isUpdatedExpense();
			
			verify(expenseBudgetSynchronizer).reapply(any(Expense.class), any(Expense.class));
			verify(expenseRepository).saveAndFlush(any(Expense.class));
		}

		@Test
		@DisplayName("Should throw exception when expense not found during update")
		void updateExpense_withNonExistentId_throwsException() {
			String errorMessage = String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID);
			
			when(expenseRepository.findById(ExpenseConstants.NonExistent.ID))
					.thenReturn(Optional.empty());

			ExpenseNotFoundException exception = assertThrows(
					ExpenseNotFoundException.class,
					() -> expenseService.updateExpense(ExpenseConstants.NonExistent.ID, expenseRequest)
			);
			
			assertEquals(errorMessage, exception.getMessage());

			verify(expenseBudgetSynchronizer, never()).reapply(any(Expense.class), any(Expense.class));
			verify(expenseRepository, never()).saveAndFlush(any(Expense.class));
		}
	}

	@Nested
	@DisplayName("Delete Expense Operations")
	class DeleteExpenseOperations {
		
		@Test
		@DisplayName("Should delete expense when found by ID")
		void deleteExpense_withValidId_deletesExpense() {
			when(expenseRepository.findById(ExpenseConstants.Default.ID))
					.thenReturn(Optional.of(testExpense));
			doNothing().when(expenseBudgetSynchronizer)
					.retract(any(Expense.class));
			doNothing()
					.when(expenseRepository)
					.deleteById(ExpenseConstants.Default.ID);
			
			expenseService.deleteExpense(ExpenseConstants.Default.ID);

			verify(expenseBudgetSynchronizer).retract(any(Expense.class));
			verify(expenseRepository).findById(ExpenseConstants.Default.ID);
			verify(expenseRepository).deleteById(ExpenseConstants.Default.ID);
		}

		@Test
		@DisplayName("Should throw exception when expense not found during delete")
		void deleteExpense_withNonExistentId_throwsException() {
			String errorMessage = String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID);
			
			when(expenseRepository.findById(ExpenseConstants.NonExistent.ID))
					.thenReturn(Optional.empty());
			
			ExpenseNotFoundException exception = assertThrows(
					ExpenseNotFoundException.class,
					() -> expenseService.deleteExpense(ExpenseConstants.NonExistent.ID)
			);
			
			assertEquals(errorMessage, exception.getMessage());
			
			verify(expenseBudgetSynchronizer, never()).retract(any(Expense.class));
			verify(expenseRepository, never()).deleteById(anyLong());
		}
	}
}