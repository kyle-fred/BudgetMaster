package com.budgetmaster.expense.controller;

import java.util.List;

import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.exception.ExpenseNotFoundException;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.expense.service.ExpenseService;
import com.budgetmaster.testsupport.constants.Messages;
import com.budgetmaster.testsupport.constants.Paths;
import com.budgetmaster.testsupport.expense.constants.ExpenseConstants;
import com.budgetmaster.testsupport.expense.factory.ExpenseFactory;
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
		testExpense = ExpenseFactory.createDefaultExpense();
		testExpenseRequest = ExpenseFactory.createDefaultExpenseRequest();
	}
	
	@Test
	void createExpense_ValidRequest_ReturnsCreated() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenReturn(testExpense);	
				
		mockMvc.perform(post(Paths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME).value(ExpenseConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(ExpenseConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(ExpenseConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_CATEGORY).value(ExpenseConstants.Default.CATEGORY.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_TYPE).value(ExpenseConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_YEAR).value(ExpenseConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH).value(ExpenseConstants.Default.MONTH));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void getExpense_ValidId_ReturnsOk() throws Exception {
		Mockito.when(expenseService.getExpenseById(ExpenseConstants.Default.ID))
				.thenReturn(testExpense);
				
		mockMvc.perform(get(Paths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME).value(ExpenseConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(ExpenseConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(ExpenseConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_CATEGORY).value(ExpenseConstants.Default.CATEGORY.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_TYPE).value(ExpenseConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_YEAR).value(ExpenseConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH).value(ExpenseConstants.Default.MONTH));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(ExpenseConstants.Default.ID);
	}

	@Test
	void getExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.getExpenseById(ExpenseConstants.NonExistent.ID))
				.thenThrow(new ExpenseNotFoundException(String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)));
				
		mockMvc.perform(get(Paths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(ExpenseConstants.NonExistent.ID);
	}
	
	@Test
	void getAllExpenses_ValidMonth_ReturnsOk() throws Exception {
		List<Expense> expenseList = List.of(testExpense, testExpense);
		
		Mockito.when(expenseService.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString()))
				.thenReturn(expenseList);
				
		mockMvc.perform(get(Paths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.param(Paths.RequestParamsConstants.REQUEST_PARAM_MONTH, ExpenseConstants.Default.YEAR_MONTH.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME_0).value(ExpenseConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME_1).value(ExpenseConstants.Default.NAME));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString());
	}
	
	@Test
	void updateExpense_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenReturn(testExpense);
				
		mockMvc.perform(put(Paths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME).value(ExpenseConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(ExpenseConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(ExpenseConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_CATEGORY).value(ExpenseConstants.Default.CATEGORY.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_TYPE).value(ExpenseConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_YEAR).value(ExpenseConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH).value(ExpenseConstants.Default.MONTH));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}

	@Test
	void updateExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenThrow(new ExpenseNotFoundException(String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)));
				
		mockMvc.perform(put(Paths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void deleteExpense_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(expenseService)
				.deleteExpense(ExpenseConstants.Default.ID);
				
		mockMvc.perform(delete(Paths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseConstants.Default.ID))
				.andExpect(status().isNoContent());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(ExpenseConstants.Default.ID);
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new ExpenseNotFoundException(String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)))
				.when(expenseService)
				.deleteExpense(ExpenseConstants.NonExistent.ID);
				
		mockMvc.perform(delete(Paths.EndpointPathConstants.ENDPOINT_EXPENSE_WITH_ID, ExpenseConstants.NonExistent.ID))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(Messages.ExpenseErrorMessageConstants.EXPENSE_NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(ExpenseConstants.NonExistent.ID);
	}
	
	@Test
	void createExpense_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new RuntimeException(Messages.CommonErrorMessageConstants.SERVICE_FAILURE));
				
		mockMvc.perform(post(Paths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_EMPTY).value(Messages.CommonErrorMessageConstants.UNEXPECTED_ERROR));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void createExpense_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new DataIntegrityViolationException(Messages.CommonErrorMessageConstants.DATABASE_CONSTRAINT));
				
		mockMvc.perform(post(Paths.EndpointPathConstants.ENDPOINT_EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_EMPTY).value(Messages.CommonErrorMessageConstants.DATABASE_CONSTRAINT));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
}