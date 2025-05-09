package com.budgetmaster.expense.controller;

import java.util.List;

import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.exception.ExpenseNotFoundException;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.expense.service.ExpenseService;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestPaths;
import com.budgetmaster.test.constants.TestData.ExpenseTestConstants;
import com.budgetmaster.test.factory.ExpenseTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
@Import(JacksonConfig.class)
public class ExpenseControllerTest {
	// -- Dependencies --
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
    private ExpenseService expenseService;

	// -- Test Objects --
	private Expense testExpense;
	private ExpenseRequest testExpenseRequest;

	// -- Setup --
	@BeforeEach
	void setUp() {
		testExpense = ExpenseTestFactory.createDefaultExpense();
		testExpenseRequest = ExpenseTestFactory.createDefaultExpenseRequest();
	}
	
	@Test
	void createExpense_ValidRequest_ReturnsCreated() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenReturn(testExpense);	
				
		mockMvc.perform(post(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(ExpenseTestConstants.Default.NAME))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(ExpenseTestConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(ExpenseTestConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_CATEGORY).value(ExpenseTestConstants.Default.CATEGORY.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(ExpenseTestConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_YEAR).value(ExpenseTestConstants.Default.YEAR))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(ExpenseTestConstants.Default.MONTH));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void getExpense_ValidId_ReturnsOk() throws Exception {
		Mockito.when(expenseService.getExpenseById(ExpenseTestConstants.Default.ID))
				.thenReturn(testExpense);
				
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseTestConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(ExpenseTestConstants.Default.NAME))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(ExpenseTestConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(ExpenseTestConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_CATEGORY).value(ExpenseTestConstants.Default.CATEGORY.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(ExpenseTestConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_YEAR).value(ExpenseTestConstants.Default.YEAR))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(ExpenseTestConstants.Default.MONTH));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(ExpenseTestConstants.Default.ID);
	}

	@Test
	void getExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.getExpenseById(ExpenseTestConstants.NonExistent.ID))
				.thenThrow(new ExpenseNotFoundException(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseTestConstants.NonExistent.ID)));
				
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseTestConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseTestConstants.NonExistent.ID)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(ExpenseTestConstants.NonExistent.ID);
	}
	
	@Test
	void getAllExpenses_ValidMonth_ReturnsOk() throws Exception {
		List<Expense> expenseList = List.of(testExpense, testExpense);
		
		Mockito.when(expenseService.getAllExpensesForMonth(ExpenseTestConstants.Default.YEAR_MONTH.toString()))
				.thenReturn(expenseList);
				
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, ExpenseTestConstants.Default.YEAR_MONTH.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME_0).value(ExpenseTestConstants.Default.NAME))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME_1).value(ExpenseTestConstants.Default.NAME));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getAllExpensesForMonth(ExpenseTestConstants.Default.YEAR_MONTH.toString());
	}
	
	@Test
	void updateExpense_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenReturn(testExpense);
				
		mockMvc.perform(put(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseTestConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(ExpenseTestConstants.Default.NAME))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(ExpenseTestConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(ExpenseTestConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_CATEGORY).value(ExpenseTestConstants.Default.CATEGORY.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(ExpenseTestConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_YEAR).value(ExpenseTestConstants.Default.YEAR))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(ExpenseTestConstants.Default.MONTH));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}

	@Test
	void updateExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenThrow(new ExpenseNotFoundException(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseTestConstants.NonExistent.ID)));
				
		mockMvc.perform(put(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseTestConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseTestConstants.NonExistent.ID)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void deleteExpense_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(expenseService)
				.deleteExpense(ExpenseTestConstants.Default.ID);
				
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseTestConstants.Default.ID))
				.andExpect(status().isNoContent());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(ExpenseTestConstants.Default.ID);
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new ExpenseNotFoundException(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseTestConstants.NonExistent.ID)))
				.when(expenseService)
				.deleteExpense(ExpenseTestConstants.NonExistent.ID);
				
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseTestConstants.NonExistent.ID))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseTestConstants.NonExistent.ID)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(ExpenseTestConstants.NonExistent.ID);
	}
	
	@Test
	void createExpense_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new RuntimeException(TestMessages.CommonErrorMessageConstants.SERVICE_FAILURE));
				
		mockMvc.perform(post(TestPaths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
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
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_EMPTY).value(TestMessages.CommonErrorMessageConstants.DATABASE_CONSTRAINT));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
}