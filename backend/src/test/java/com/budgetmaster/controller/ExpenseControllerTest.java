package com.budgetmaster.controller;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.SupportedCurrency;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.ExpenseNotFoundException;
import com.budgetmaster.service.ExpenseService;
import com.budgetmaster.test.constants.TestData.ExpenseTestData;
import com.budgetmaster.test.constants.TestData.SharedTestData;
import com.budgetmaster.test.constants.TestMessages.CommonErrorMessages;
import com.budgetmaster.test.constants.TestMessages.ExpenseErrorMessages;
import com.budgetmaster.test.constants.TestPaths.TestExpensePaths;
import com.budgetmaster.test.constants.TestPaths.TestJsonPaths;
import com.budgetmaster.test.constants.TestPaths.TestRequestParams;
import com.budgetmaster.model.Expense;
import com.budgetmaster.model.value.Money;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;

@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {
	// -- Dependencies --
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
    private ExpenseService expenseService;

	// -- Test Data --
	private static final Long testId = SharedTestData.TEST_EXISTING_ID;
	private static final Long testNonExistentId = SharedTestData.TEST_NON_EXISTING_ID;
	private static final String testName = ExpenseTestData.TEST_NAME;
	private static final BigDecimal testAmount = ExpenseTestData.TEST_AMOUNT;
	private static final Currency testCurrency = SupportedCurrency.GBP.getCurrency();
	private static final TransactionType testType = TransactionType.ONE_TIME;
	private static final ExpenseCategory testCategory = ExpenseCategory.MISCELLANEOUS;
	private static final String testMonth = SharedTestData.TEST_EXISTING_MONTH_YEAR_STRING;
	private static final YearMonth testYearMonth = SharedTestData.TEST_EXISTING_MONTH_YEAR;

	// -- Test Objects --
	private ExpenseRequest expenseRequest;
	private Expense expense;
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
	
		expense = new Expense();
		expense.setId(testId);
		expense.setName(testName);
		expense.setMoney(Money.of(testAmount, testCurrency));
		expense.setCategory(testCategory);
		expense.setType(testType);
		expense.setMonth(testYearMonth);
	}
	
	// -- Create Expense Tests --
	
	@Test
	void createExpense_ValidRequest_ReturnsCreated() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenReturn(expense);	
				
		mockMvc.perform(post(TestExpensePaths.TEST_EXPENSE_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_CATEGORY).value(testCategory.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONTH).value(testMonth));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	// -- Get Expense Tests --
	
	@Test
	void getExpense_ValidId_ReturnsOk() throws Exception {
		Mockito.when(expenseService.getExpenseById(testId))
				.thenReturn(expense);
				
		mockMvc.perform(get(TestExpensePaths.TEST_EXPENSE_ENDPOINT_WITH_ID, testId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_CATEGORY).value(testCategory.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONTH).value(testMonth));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(testId);
	}

	@Test
	void getExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.getExpenseById(testNonExistentId))
				.thenThrow(new ExpenseNotFoundException(String.format(ExpenseErrorMessages.TEST_MESSAGE_EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		mockMvc.perform(get(TestExpensePaths.TEST_EXPENSE_ENDPOINT_WITH_ID, testNonExistentId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_ERROR).value(String.format(ExpenseErrorMessages.TEST_MESSAGE_EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(testNonExistentId);
	}

	// -- Get All Expenses Tests --
	
	@Test
	void getAllExpenses_ValidMonth_ReturnsOk() throws Exception {
		List<Expense> expenseList = List.of(expense, expense);
		
		Mockito.when(expenseService.getAllExpensesForMonth(testMonth))
				.thenReturn(expenseList);
				
		mockMvc.perform(get(TestExpensePaths.TEST_EXPENSE_ENDPOINT)
				.param(TestRequestParams.TEST_PARAM_MONTH, testMonth)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME_0).value(testName))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME_1).value(testName));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getAllExpensesForMonth(testMonth);
	}
	
	// -- Update Expense Tests --
	
	@Test
	void updateExpense_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenReturn(expense);
				
		mockMvc.perform(put(TestExpensePaths.TEST_EXPENSE_ENDPOINT_WITH_ID, testId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_CATEGORY).value(testCategory.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONTH).value(testMonth));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}

	@Test
	void updateExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenThrow(new ExpenseNotFoundException(String.format(ExpenseErrorMessages.TEST_MESSAGE_EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		mockMvc.perform(put(TestExpensePaths.TEST_EXPENSE_ENDPOINT_WITH_ID, testNonExistentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_ERROR).value(String.format(ExpenseErrorMessages.TEST_MESSAGE_EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}
	
	// -- Delete Expense Tests --
	
	@Test
	void deleteExpense_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(expenseService)
				.deleteExpense(testId);
				
		mockMvc.perform(delete(TestExpensePaths.TEST_EXPENSE_ENDPOINT_WITH_ID, testId))
				.andExpect(status().isNoContent());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(testId);
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new ExpenseNotFoundException(String.format(ExpenseErrorMessages.TEST_MESSAGE_EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)))
				.when(expenseService)
				.deleteExpense(testNonExistentId);
				
		mockMvc.perform(delete(TestExpensePaths.TEST_EXPENSE_ENDPOINT_WITH_ID, testNonExistentId))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_ERROR).value(String.format(ExpenseErrorMessages.TEST_MESSAGE_EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(testNonExistentId);
	}
	
	// -- Error Handling Tests --
	
	@Test
	void createExpense_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new RuntimeException(CommonErrorMessages.TEST_MESSAGE_SERVICE_FAILURE));
				
		mockMvc.perform(post(TestExpensePaths.TEST_EXPENSE_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_EMPTY).value(CommonErrorMessages.TEST_MESSAGE_INTERNAL_SERVER_ERROR));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void createExpense_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new DataIntegrityViolationException(CommonErrorMessages.TEST_MESSAGE_DATABASE_CONSTRAINT_VIOLATION));
				
		mockMvc.perform(post(TestExpensePaths.TEST_EXPENSE_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_EMPTY).value(CommonErrorMessages.TEST_MESSAGE_DATABASE_CONSTRAINT_VIOLATION));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
}