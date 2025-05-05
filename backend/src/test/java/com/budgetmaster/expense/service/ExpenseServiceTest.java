package com.budgetmaster.expense.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.enums.ExpenseCategory;
import com.budgetmaster.expense.exception.ExpenseNotFoundException;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.expense.repository.ExpenseRepository;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData;
import com.budgetmaster.test.constants.TestMessages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class ExpenseServiceTest {
	// -- Dependencies --
	private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
	private final ExpenseService expenseService = new ExpenseService(expenseRepository);
	
	// -- Test Data --
	private static final Long testId = TestData.CommonTestDataConstants.ID_EXISTING;
	private static final Long testIdNonExistent = TestData.CommonTestDataConstants.ID_NON_EXISTING;
	private static final String testName = TestData.ExpenseTestDataConstants.NAME;
	private static final BigDecimal testAmount = TestData.MoneyDtoTestDataConstants.AMOUNT;
	private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
	private static final ExpenseCategory testCategory = TestData.ExpenseTestDataConstants.CATEGORY_MISCELLANEOUS;
	private static final TransactionType testType = TestData.ExpenseTestDataConstants.TYPE_ONE_TIME;
	private static final String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;
	private static final YearMonth testYearMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
	
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

		assertNotNull(savedExpense);
		assertEquals(testName, savedExpense.getName());
		assertEquals(testAmount, savedExpense.getMoney().getAmount());
		assertEquals(testCurrency, savedExpense.getMoney().getCurrency());
		assertEquals(testCategory, savedExpense.getCategory());
		assertEquals(testType, savedExpense.getType());
		assertEquals(testYearMonth, savedExpense.getMonth());

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

			assertNotNull(result);
			assertEquals(2, result.size());
			assertEquals(testName, result.get(0).getName());
			assertEquals(testName, result.get(1).getName());

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
		
		assertNotNull(retrievedExpense);
		assertEquals(testName, retrievedExpense.getName());
		assertEquals(testAmount, retrievedExpense.getMoney().getAmount());
		assertEquals(testCurrency, retrievedExpense.getMoney().getCurrency());
		assertEquals(testCategory, retrievedExpense.getCategory());
		assertEquals(testType, retrievedExpense.getType());
		assertEquals(testYearMonth, retrievedExpense.getMonth());
		
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
		updatedExpenseRequest.setName(TestData.ExpenseTestDataConstants.NAME_UPDATED);
		updatedExpenseRequest.setMoney(moneyRequest);
		updatedExpenseRequest.setCategory(TestData.ExpenseTestDataConstants.CATEGORY_UPDATED);
		updatedExpenseRequest.setType(TestData.ExpenseTestDataConstants.TYPE_UPDATED);
		updatedExpenseRequest.setMonth(TestData.MonthTestDataConstants.MONTH_STRING_NON_EXISTING);

		Expense updatedExpense = expenseService.updateExpense(testId, updatedExpenseRequest);

		assertNotNull(updatedExpense);
		assertEquals(updatedExpenseRequest.getName(), updatedExpense.getName());
		assertEquals(updatedExpenseRequest.getMoney().getAmount(), updatedExpense.getMoney().getAmount());
		assertEquals(updatedExpenseRequest.getMoney().getCurrency(), updatedExpense.getMoney().getCurrency());
		assertEquals(updatedExpenseRequest.getCategory(), updatedExpense.getCategory());
		assertEquals(updatedExpenseRequest.getType(), updatedExpense.getType());
		assertEquals(updatedExpenseRequest.getMonth(), updatedExpense.getMonth().toString());
		
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
		String errorMessage = TestMessages.CommonErrorMessageConstants.DUPLICATE_ENTRY;
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
		String errorMessage = String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, testIdNonExistent);
		Mockito.when(expenseRepository.findById(testIdNonExistent))
				.thenReturn(Optional.empty());
		
		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.getExpenseById(testIdNonExistent),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void getAllExpenses_NoExpenses_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_BY_MONTH, testYearMonth);
		
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
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}
	
	@Test
	void updateExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, testIdNonExistent);
		Mockito.when(expenseRepository.findById(testIdNonExistent))
				.thenReturn(Optional.empty());

		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.updateExpense(testIdNonExistent, expenseRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(expenseRepository, Mockito.never())
				.saveAndFlush(Mockito.any(Expense.class));
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, testIdNonExistent);
		Mockito.when(expenseRepository.findById(testIdNonExistent))
				.thenReturn(Optional.empty());
		
		ExpenseNotFoundException exception = assertThrows(
				ExpenseNotFoundException.class,
				() -> expenseService.deleteExpense(testIdNonExistent),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(expenseRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}