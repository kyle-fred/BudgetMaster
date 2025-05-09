package com.budgetmaster.expense.service;

import java.util.List;
import java.util.Optional;

import com.budgetmaster.budget.service.logic.ExpenseBudgetSynchronizer;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.exception.ExpenseNotFoundException;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.expense.repository.ExpenseRepository;
import com.budgetmaster.testsupport.constants.Messages;
import com.budgetmaster.testsupport.expense.constants.ExpenseConstants;
import com.budgetmaster.testsupport.expense.factory.ExpenseFactory;

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
	// -- Dependencies --
	private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
	private final ExpenseBudgetSynchronizer expenseBudgetSynchronizer = mock(ExpenseBudgetSynchronizer.class);
	private final ExpenseService expenseService = new ExpenseService(expenseRepository, expenseBudgetSynchronizer);
	
	// -- Test Objects --
	private ExpenseRequest expenseRequest;
	private Expense testExpense;
	
	@BeforeEach
	void setUp() {
		testExpense = ExpenseFactory.createDefaultExpense();
		expenseRequest = ExpenseFactory.createDefaultExpenseRequest();
	}
	
	@Test
	void createExpense_ValidRequest_ReturnsCreated() {
		Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
				.thenReturn(testExpense);
		Mockito.doNothing().when(expenseBudgetSynchronizer)
				.apply(Mockito.any(Expense.class));
		
		Expense savedExpense = expenseService.createExpense(expenseRequest);

		assertNotNull(savedExpense);
		assertEquals(ExpenseConstants.Default.NAME, savedExpense.getName());
		assertEquals(ExpenseConstants.Default.AMOUNT, savedExpense.getMoney().getAmount());
		assertEquals(ExpenseConstants.Default.CURRENCY, savedExpense.getMoney().getCurrency());
		assertEquals(ExpenseConstants.Default.CATEGORY, savedExpense.getCategory());
		assertEquals(ExpenseConstants.Default.TYPE, savedExpense.getType());
		assertEquals(ExpenseConstants.Default.YEAR_MONTH, savedExpense.getMonth());

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

			assertNotNull(result);
			assertEquals(2, result.size());
			assertEquals(ExpenseConstants.Default.NAME, result.get(0).getName());
			assertEquals(ExpenseConstants.Default.NAME, result.get(1).getName());

			Mockito.verify(expenseRepository, Mockito.times(1))
					.findByMonth(ExpenseConstants.Default.YEAR_MONTH);
		}
	}
	
	@Test
	void getExpense_ValidId_ReturnsOk() {
		Mockito.when(expenseRepository.findById(ExpenseConstants.Default.ID))
				.thenReturn(Optional.of(testExpense));
		
		Expense retrievedExpense = expenseService.getExpenseById(ExpenseConstants.Default.ID);
		
		assertNotNull(retrievedExpense);
		assertEquals(ExpenseConstants.Default.NAME, retrievedExpense.getName());
		assertEquals(ExpenseConstants.Default.AMOUNT, retrievedExpense.getMoney().getAmount());
		assertEquals(ExpenseConstants.Default.CURRENCY, retrievedExpense.getMoney().getCurrency());
		assertEquals(ExpenseConstants.Default.CATEGORY, retrievedExpense.getCategory());
		assertEquals(ExpenseConstants.Default.TYPE, retrievedExpense.getType());
		assertEquals(ExpenseConstants.Default.YEAR_MONTH, retrievedExpense.getMonth());
		
		Mockito.verify(expenseRepository, Mockito.times(1))
				.findById(ExpenseConstants.Default.ID);
	}
	
	@Test
	void updateExpense_ValidRequest_ReturnsOk() {
		ExpenseRequest updatedExpenseRequest = ExpenseFactory.createUpdatedExpenseRequest();

		Mockito.when(expenseRepository.findById(ExpenseConstants.Default.ID))
				.thenReturn(Optional.of(testExpense));
		Mockito.doNothing().when(expenseBudgetSynchronizer)
				.reapply(Mockito.any(Expense.class), Mockito.any(Expense.class));
		Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
				.thenReturn(testExpense);

		Expense updatedExpense = expenseService.updateExpense(ExpenseConstants.Default.ID, updatedExpenseRequest);

		assertNotNull(updatedExpense);
		assertEquals(updatedExpenseRequest.getName(), updatedExpense.getName());
		assertEquals(updatedExpenseRequest.getMoney().getAmount(), updatedExpense.getMoney().getAmount());
		assertEquals(updatedExpenseRequest.getMoney().getCurrency(), updatedExpense.getMoney().getCurrency());
		assertEquals(updatedExpenseRequest.getCategory(), updatedExpense.getCategory());
		assertEquals(updatedExpenseRequest.getType(), updatedExpense.getType());
		assertEquals(updatedExpenseRequest.getMonth(), updatedExpense.getMonth().toString());
		
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
		String errorMessage = Messages.CommonErrorMessageConstants.DUPLICATE_ENTRY;
		Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
				.thenThrow(new DataIntegrityViolationException(errorMessage));
		
		DataIntegrityViolationException exception = assertThrows(
				DataIntegrityViolationException.class,
				() -> expenseService.createExpense(expenseRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void getExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID);
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
		String errorMessage = String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_BY_MONTH, ExpenseConstants.Default.YEAR_MONTH.toString());
		
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
		String errorMessage = String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID);
		Mockito.when(expenseRepository.findById(ExpenseConstants.NonExistent.ID))
				.thenReturn(Optional.empty());

		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.updateExpense(ExpenseConstants.NonExistent.ID, expenseRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(expenseRepository, Mockito.never())
				.saveAndFlush(Mockito.any(Expense.class));
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID);
		Mockito.when(expenseRepository.findById(ExpenseConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		
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