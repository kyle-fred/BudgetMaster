package com.budgetmaster.service;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.repository.ExpenseRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.model.Expense;
import com.budgetmaster.model.value.Money;
import com.budgetmaster.exception.ExpenseNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

class ExpenseServiceTest {
	// -- Dependencies --
	private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
	private final ExpenseService expenseService = new ExpenseService(expenseRepository);
	
	// -- Test Data --
	private static final Long testId = 1L;
	private static final String testName = "TEST EXPENSE";
	private static final BigDecimal testAmount = new BigDecimal("123.45");
	private static final Currency testCurrency = Currency.getInstance("GBP");
    private static final ExpenseCategory testCategory = ExpenseCategory.MISCELLANEOUS;
	private static final TransactionType testType = TransactionType.ONE_TIME;
	private static final String testMonth = "2000-01";
	private static final YearMonth testYearMonth = YearMonth.of(2000, 1);
	
	// -- Test Objects --
	private ExpenseRequest expenseRequest;
	private Expense testExpense;
	private MoneyRequest moneyRequest;
	
	// -- Setup --
	
	@BeforeEach
	void setUp() {
		// Setup MoneyRequest
		moneyRequest = new MoneyRequest();
		moneyRequest.setAmount(testAmount);
		moneyRequest.setCurrency(testCurrency);
		
		// Setup ExpenseRequest
		expenseRequest = new ExpenseRequest();
		expenseRequest.setName(testName);
		expenseRequest.setMoney(moneyRequest);
		expenseRequest.setCategory(testCategory);
		expenseRequest.setType(testType);
		expenseRequest.setMonth(testMonth);
		
		// Setup Expense
		testExpense = new Expense();
		testExpense.setId(testId);
		testExpense.setName(testName);
		testExpense.setMoney(Money.of(testAmount, testCurrency));
		testExpense.setCategory(testCategory);
		testExpense.setType(testType);
		testExpense.setMonth(testYearMonth);
	}
	
	// -- Create Expense Tests --
	
	@Test
	void createExpense_ValidRequest_ReturnsCreated() {
		Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
				.thenReturn(testExpense);
		
		Expense savedExpense = expenseService.createExpense(expenseRequest);

		assertNotNull(savedExpense, "Expense should not be null");
		assertEquals(testName, savedExpense.getName(), "Name should be equal to the test value");
		assertEquals(testAmount, savedExpense.getMoney().getAmount(), "Amount should be equal to the test value");
		assertEquals(testCurrency, savedExpense.getMoney().getCurrency(), "Currency should be equal to the test value");
		assertEquals(testCategory, savedExpense.getCategory(), "Category should be equal to the test value");
		assertEquals(testType, savedExpense.getType(), "Type should be equal to the test value");
		assertEquals(testYearMonth, savedExpense.getMonth(), "Month should be equal to the test value");

		Mockito.verify(expenseRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Expense.class));
	}
	
	// -- Get All Expenses Tests --
	
	@Test
	void getAllExpenses_ValidMonth_ReturnsOk() {
		List<Expense> expenses = List.of(testExpense, testExpense);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(testMonth))
					.thenReturn(testYearMonth);
			Mockito.when(expenseRepository.findByMonth(testYearMonth))
					.thenReturn(expenses);

			List<Expense> result = expenseService.getAllExpensesForMonth(testMonth);

			assertNotNull(result, "Result should not be null");
			assertEquals(2, result.size(), "Result should have 2 expenses");
			assertEquals(testName, result.get(0).getName(), "Name should be equal to the test value");
			assertEquals(testName, result.get(1).getName(), "Name should be equal to the test value");

			Mockito.verify(expenseRepository, Mockito.times(1))
					.findByMonth(testYearMonth);
		}
	}
	
	// -- Get Expense Tests --
	
	@Test
	void getExpense_ValidId_ReturnsOk() {
		Mockito.when(expenseRepository.findById(testId))
				.thenReturn(Optional.of(testExpense));
		
		Expense retrievedExpense = expenseService.getExpenseById(testId);
		
		assertNotNull(retrievedExpense, "Expense should not be null");
		assertEquals(testName, retrievedExpense.getName(), "Name should be equal to the test value");
		assertEquals(testAmount, retrievedExpense.getMoney().getAmount(), "Amount should be equal to the test value");
		assertEquals(testCurrency, retrievedExpense.getMoney().getCurrency(), "Currency should be equal to the test value");
		assertEquals(testCategory, retrievedExpense.getCategory(), "Category should be equal to the test value");
		assertEquals(testType, retrievedExpense.getType(), "Type should be equal to the test value");
		assertEquals(testYearMonth, retrievedExpense.getMonth(), "Month should be equal to the test value");
		
		Mockito.verify(expenseRepository, Mockito.times(1))
				.findById(testId);
	}
	
	// -- Update Expense Tests --
	
	@Test
	void updateExpense_ValidRequest_ReturnsOk() {
		Mockito.when(expenseRepository.findById(testId))
				.thenReturn(Optional.of(testExpense));
		Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
				.thenReturn(testExpense);

		ExpenseRequest updatedExpenseRequest = new ExpenseRequest();
		updatedExpenseRequest.setName(testName + " UPDATED");
		updatedExpenseRequest.setMoney(moneyRequest);
		updatedExpenseRequest.setCategory(testCategory);
		updatedExpenseRequest.setType(testType);
		updatedExpenseRequest.setMonth(testMonth);

		Expense updatedExpense = expenseService.updateExpense(testId, updatedExpenseRequest);

		assertNotNull(updatedExpense, "Updated expense should not be null");
		assertEquals(updatedExpenseRequest.getName(), updatedExpense.getName(), "Name should be equal to the updated value");
		assertEquals(updatedExpenseRequest.getMoney().getAmount(), updatedExpense.getMoney().getAmount(), "Amount should be equal to the updated value");
		assertEquals(updatedExpenseRequest.getMoney().getCurrency(), updatedExpense.getMoney().getCurrency(), "Currency should be equal to the updated value");
		assertEquals(updatedExpenseRequest.getCategory(), updatedExpense.getCategory(), "Category should be equal to the updated value");
		assertEquals(updatedExpenseRequest.getType(), updatedExpense.getType(), "Type should be equal to the updated value");
		assertEquals(updatedExpenseRequest.getMonth(), updatedExpense.getMonth().toString(), "Month should be equal to the updated value");
		
		Mockito.verify(expenseRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Expense.class));
	}
	
	// -- Delete Expense Tests --
	
	@Test
	void deleteExpense_ValidId_ReturnsNoContent() {
		Mockito.when(expenseRepository.findById(testId))
				.thenReturn(Optional.of(testExpense));
		Mockito.doNothing()
				.when(expenseRepository)
				.deleteById(testId);
		
		expenseService.deleteExpense(testId);

		Mockito.verify(expenseRepository, Mockito.times(1))
				.findById(testId);
		Mockito.verify(expenseRepository, Mockito.times(1))
				.deleteById(testId);
	}
	
	// -- Error Handling Tests --
	
	@Test
	void createExpense_ServiceError_ReturnsInternalServerError() {
		String errorMessage = "Duplicate Entry";
		Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
				.thenThrow(new DataIntegrityViolationException(errorMessage));
		
		DataIntegrityViolationException exception = assertThrows(
				DataIntegrityViolationException.class,
				() -> expenseService.createExpense(expenseRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
	}
	
	@Test
	void getExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = "Expense not found with id: 99";
		Mockito.when(expenseRepository.findById(99L))
				.thenReturn(Optional.empty());
		
		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.getExpenseById(99L),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
	}
	
	@Test
	void getAllExpenses_NoExpenses_ReturnsNotFound() {
		String errorMessage = "No expenses found for month: " + testYearMonth;
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(testMonth))
					.thenReturn(testYearMonth);
			Mockito.when(expenseRepository.findByMonth(testYearMonth))
					.thenThrow(new ExpenseNotFoundException(errorMessage));

			ExpenseNotFoundException exception = assertThrows(
					ExpenseNotFoundException.class,
					() -> expenseService.getAllExpensesForMonth(testMonth),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
		}
	}
	
	@Test
	void updateExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = "Expense not found with id: 99";
		Mockito.when(expenseRepository.findById(99L))
				.thenReturn(Optional.empty());

		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.updateExpense(99L, expenseRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
		Mockito.verify(expenseRepository, Mockito.never())
				.saveAndFlush(Mockito.any(Expense.class));
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = "Expense not found with id: 99";
		Mockito.when(expenseRepository.findById(99L))
				.thenReturn(Optional.empty());
		
		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.deleteExpense(99L),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
		Mockito.verify(expenseRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}