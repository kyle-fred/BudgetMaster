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
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@Import(JacksonConfig.class)
public class ExpenseServiceTest {

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
	
	@Test
	void createExpense_ValidRequest_ReturnsCreated() {
		Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
				.thenReturn(testExpense);
		Mockito.doNothing().when(expenseBudgetSynchronizer)
				.apply(Mockito.any(Expense.class));
		
		Expense savedExpense = expenseService.createExpense(expenseRequest);

		ExpenseModelAssertions.assertExpense(savedExpense)
			.isDefaultExpense();

		Mockito.verify(expenseBudgetSynchronizer, Mockito.times(1))
				.apply(Mockito.any(Expense.class));
		Mockito.verify(expenseRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Expense.class));
	}
	
	@Test
	void getAllExpenses_ValidMonth_ReturnsOk() {
		List<Expense> expenses = List.of(testExpense, testExpense);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(ExpenseConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(ExpenseConstants.Default.YEAR_MONTH);
			Mockito.when(expenseRepository.findByMonth(ExpenseConstants.Default.YEAR_MONTH))
					.thenReturn(expenses);

			List<Expense> result = expenseService.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString());

			ExpenseListAssertions.assertExpenses(result)
				.hasSize(2)
				.first()
				.isDefaultExpense();

			Mockito.verify(expenseRepository, Mockito.times(1))
					.findByMonth(ExpenseConstants.Default.YEAR_MONTH);
		}
	}
	
	@Test
	void getExpense_ValidId_ReturnsOk() {
		Mockito.when(expenseRepository.findById(ExpenseConstants.Default.ID))
				.thenReturn(Optional.of(testExpense));
		
		Expense retrievedExpense = expenseService.getExpenseById(ExpenseConstants.Default.ID);
		
		ExpenseModelAssertions.assertExpense(retrievedExpense)
			.isDefaultExpense();
		
		Mockito.verify(expenseRepository, Mockito.times(1))
				.findById(ExpenseConstants.Default.ID);
	}
	
	@Test
	void updateExpense_ValidRequest_ReturnsOk() {
		ExpenseRequest updatedExpenseRequest = ExpenseRequestBuilder.updatedExpenseRequest().buildRequest();

		Mockito.when(expenseRepository.findById(ExpenseConstants.Default.ID))
				.thenReturn(Optional.of(testExpense));
		Mockito.doNothing().when(expenseBudgetSynchronizer)
				.reapply(Mockito.any(Expense.class), Mockito.any(Expense.class));
		Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
				.thenReturn(testExpense);

		Expense updatedExpense = expenseService.updateExpense(ExpenseConstants.Default.ID, updatedExpenseRequest);

		ExpenseModelAssertions.assertExpense(updatedExpense)
			.isUpdatedExpense();
		
		Mockito.verify(expenseBudgetSynchronizer, Mockito.times(1))
				.reapply(Mockito.any(Expense.class), Mockito.any(Expense.class));
		Mockito.verify(expenseRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Expense.class));
	}
	
	@Test
	void deleteExpense_ValidId_ReturnsNoContent() {
		Mockito.when(expenseRepository.findById(ExpenseConstants.Default.ID))
				.thenReturn(Optional.of(testExpense));
		Mockito.doNothing().when(expenseBudgetSynchronizer)
				.retract(Mockito.any(Expense.class));
		Mockito.doNothing()
				.when(expenseRepository)
				.deleteById(ExpenseConstants.Default.ID);
		
		expenseService.deleteExpense(ExpenseConstants.Default.ID);

		Mockito.verify(expenseBudgetSynchronizer, Mockito.times(1))
				.retract(Mockito.any(Expense.class));
		Mockito.verify(expenseRepository, Mockito.times(1))
				.findById(ExpenseConstants.Default.ID);
		Mockito.verify(expenseRepository, Mockito.times(1))
				.deleteById(ExpenseConstants.Default.ID);
	}
	
	@Test
	void createExpense_ServiceError_ReturnsInternalServerError() {
		String errorMessage = ErrorCode.DATABASE_ERROR.getMessage();

		Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
				.thenThrow(new DataIntegrityViolationException(errorMessage));
		
		DataIntegrityViolationException exception = assertThrows(
				DataIntegrityViolationException.class,
				() -> expenseService.createExpense(expenseRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());

		Mockito.verify(expenseBudgetSynchronizer, Mockito.never())
				.apply(Mockito.any(Expense.class));
	}
	
	@Test
	void getExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID);
		
		Mockito.when(expenseRepository.findById(ExpenseConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		
		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.getExpenseById(ExpenseConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void getAllExpenses_NoExpenses_ReturnsNotFound() {
		String errorMessage = String.format(ErrorConstants.Expense.NOT_FOUND_BY_MONTH, ExpenseConstants.Default.YEAR_MONTH.toString());
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(ExpenseConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(ExpenseConstants.Default.YEAR_MONTH);
			Mockito.when(expenseRepository.findByMonth(ExpenseConstants.Default.YEAR_MONTH))
					.thenThrow(new ExpenseNotFoundException(errorMessage));

			ExpenseNotFoundException exception = assertThrows(
					ExpenseNotFoundException.class,
					() -> expenseService.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString()),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}
	
	@Test
	void updateExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID);
		
		Mockito.when(expenseRepository.findById(ExpenseConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		Mockito.doNothing().when(expenseBudgetSynchronizer)
				.reapply(Mockito.any(Expense.class), Mockito.any(Expense.class));

		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.updateExpense(ExpenseConstants.NonExistent.ID, expenseRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());

		Mockito.verify(expenseBudgetSynchronizer, Mockito.never())
				.reapply(Mockito.any(Expense.class), Mockito.any(Expense.class));
		Mockito.verify(expenseRepository, Mockito.never())
				.saveAndFlush(Mockito.any(Expense.class));
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID);
		
		Mockito.when(expenseRepository.findById(ExpenseConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		Mockito.doNothing().when(expenseBudgetSynchronizer)
				.retract(Mockito.any(Expense.class));
		
		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.deleteExpense(ExpenseConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		
		Mockito.verify(expenseBudgetSynchronizer, Mockito.never())
				.retract(Mockito.any(Expense.class));
		Mockito.verify(expenseRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}