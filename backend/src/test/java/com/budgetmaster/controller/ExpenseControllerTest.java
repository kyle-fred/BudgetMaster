package com.budgetmaster.controller;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.ExpenseNotFoundException;
import com.budgetmaster.model.Expense;
import com.budgetmaster.model.value.Money;
import com.budgetmaster.service.ExpenseService;
import com.budgetmaster.test.constants.TestData;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestPaths;

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
	private static final Long testId = TestData.CommonTestDataConstants.ID_EXISTING;
	private static final Long testNonExistentId = TestData.CommonTestDataConstants.ID_NON_EXISTING;
	private static final String testName = TestData.ExpenseTestDataConstants.NAME;
	private static final BigDecimal testAmount = TestData.ExpenseTestDataConstants.AMOUNT;
	private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
	private static final TransactionType testType = TestData.ExpenseTestDataConstants.TYPE_ONE_TIME;
	private static final ExpenseCategory testCategory = TestData.ExpenseTestDataConstants.CATEGORY_MISCELLANEOUS;
	private static final String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;
	private static final YearMonth testYearMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;

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
				
		mockMvc.perform(post(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_CATEGORY).value(testCategory.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(testMonth));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	// -- Get Expense Tests --
	
	@Test
	void getExpense_ValidId_ReturnsOk() throws Exception {
		Mockito.when(expenseService.getExpenseById(testId))
				.thenReturn(expense);
				
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, testId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_CATEGORY).value(testCategory.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(testMonth));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(testId);
	}

	@Test
	void getExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.getExpenseById(testNonExistentId))
				.thenThrow(new ExpenseNotFoundException(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, testNonExistentId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(testNonExistentId);
	}

	// -- Get All Expenses Tests --
	
	@Test
	void getAllExpenses_ValidMonth_ReturnsOk() throws Exception {
		List<Expense> expenseList = List.of(expense, expense);
		
		Mockito.when(expenseService.getAllExpensesForMonth(testMonth))
				.thenReturn(expenseList);
				
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, testMonth)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME_0).value(testName))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME_1).value(testName));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getAllExpensesForMonth(testMonth);
	}
	
	// -- Update Expense Tests --
	
	@Test
	void updateExpense_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenReturn(expense);
				
		mockMvc.perform(put(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, testId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_CATEGORY).value(testCategory.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(testMonth));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}

	@Test
	void updateExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenThrow(new ExpenseNotFoundException(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		mockMvc.perform(put(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, testNonExistentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}
	
	// -- Delete Expense Tests --
	
	@Test
	void deleteExpense_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(expenseService)
				.deleteExpense(testId);
				
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, testId))
				.andExpect(status().isNoContent());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(testId);
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new ExpenseNotFoundException(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)))
				.when(expenseService)
				.deleteExpense(testNonExistentId);
				
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, testNonExistentId))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, testNonExistentId)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(testNonExistentId);
	}
	
	// -- Error Handling Tests --
	
	@Test
	void createExpense_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new RuntimeException(TestMessages.CommonErrorMessageConstants.SERVICE_FAILURE));
				
		mockMvc.perform(post(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_EMPTY).value(TestMessages.CommonErrorMessageConstants.UNEXPECTED_ERROR));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void createExpense_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new DataIntegrityViolationException(TestMessages.CommonErrorMessageConstants.DATABASE_CONSTRAINT));
				
		mockMvc.perform(post(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_EMPTY).value(TestMessages.CommonErrorMessageConstants.DATABASE_CONSTRAINT));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
}