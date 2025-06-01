package com.budgetmaster.application.controller;

import java.util.List;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.application.exception.ExpenseNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.service.ExpenseService;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.PathConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
	
	@SuppressWarnings("removal")
	@MockBean
    private ExpenseService expenseService;

	// -- Test Objects --
	private Expense testExpense;
	private ExpenseRequest testExpenseRequest;

	// -- Setup --
	@BeforeEach
	void setUp() {
		testExpense = ExpenseBuilder.defaultExpense().build();
		testExpenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();
	}
	
	@Test
	void createExpense_ValidRequest_ReturnsCreated() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenReturn(testExpense);	
				
		mockMvc.perform(post(PathConstants.Endpoints.EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.NAME).value(ExpenseConstants.Default.NAME))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONEY_AMOUNT).value(ExpenseConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONEY_CURRENCY).value(ExpenseConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.CATEGORY).value(ExpenseConstants.Default.CATEGORY.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.TYPE).value(ExpenseConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONTH_YEAR).isArray())
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.YEAR).value(ExpenseConstants.Default.YEAR))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONTH).value(ExpenseConstants.Default.MONTH));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void getExpense_ValidId_ReturnsOk() throws Exception {
		Mockito.when(expenseService.getExpenseById(ExpenseConstants.Default.ID))
				.thenReturn(testExpense);
				
		mockMvc.perform(get(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.NAME).value(ExpenseConstants.Default.NAME))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONEY_AMOUNT).value(ExpenseConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONEY_CURRENCY).value(ExpenseConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.CATEGORY).value(ExpenseConstants.Default.CATEGORY.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.TYPE).value(ExpenseConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONTH_YEAR).isArray())
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.YEAR).value(ExpenseConstants.Default.YEAR))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONTH).value(ExpenseConstants.Default.MONTH));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(ExpenseConstants.Default.ID);
	}

	@Test
	void getExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.getExpenseById(ExpenseConstants.NonExistent.ID))
				.thenThrow(new ExpenseNotFoundException(String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)));
				
		mockMvc.perform(get(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(String.format(PathConstants.Error.Expense.URI_WITH_ID, ExpenseConstants.NonExistent.ID)))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(ExpenseConstants.NonExistent.ID);
	}
	
	@Test
	void getAllExpenses_ValidMonth_ReturnsOk() throws Exception {
		List<Expense> expenseList = List.of(testExpense, testExpense);
		
		Mockito.when(expenseService.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString()))
				.thenReturn(expenseList);
				
		mockMvc.perform(get(PathConstants.Endpoints.EXPENSE)
				.param(PathConstants.RequestParams.MONTH, ExpenseConstants.Default.YEAR_MONTH.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.FIRST_NAME).value(ExpenseConstants.Default.NAME))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.SECOND_NAME).value(ExpenseConstants.Default.NAME));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString());
	}
	
	@Test
	void updateExpense_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenReturn(testExpense);
				
		mockMvc.perform(put(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.NAME).value(ExpenseConstants.Default.NAME))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONEY_AMOUNT).value(ExpenseConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONEY_CURRENCY).value(ExpenseConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.CATEGORY).value(ExpenseConstants.Default.CATEGORY.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.TYPE).value(ExpenseConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONTH_YEAR).isArray())
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.YEAR).value(ExpenseConstants.Default.YEAR))
				.andExpect(jsonPath(PathConstants.JsonProperties.Expense.MONTH).value(ExpenseConstants.Default.MONTH));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}

	@Test
	void updateExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenThrow(new ExpenseNotFoundException(String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)));
				
		mockMvc.perform(put(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(String.format(PathConstants.Error.Expense.URI_WITH_ID, ExpenseConstants.NonExistent.ID)))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void deleteExpense_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(expenseService)
				.deleteExpense(ExpenseConstants.Default.ID);
				
		mockMvc.perform(delete(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.Default.ID))
				.andExpect(status().isNoContent());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(ExpenseConstants.Default.ID);
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new ExpenseNotFoundException(String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)))
				.when(expenseService)
				.deleteExpense(ExpenseConstants.NonExistent.ID);
				
		mockMvc.perform(delete(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.NonExistent.ID))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(String.format(PathConstants.Error.Expense.URI_WITH_ID, ExpenseConstants.NonExistent.ID)))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(ExpenseConstants.NonExistent.ID);
	}
	
	@Test
	void createExpense_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new RuntimeException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
				
		mockMvc.perform(post(PathConstants.Endpoints.EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.INTERNAL_SERVER_ERROR.name()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(PathConstants.Error.Expense.URI))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void createExpense_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new DataIntegrityViolationException(ErrorCode.DATABASE_ERROR.getMessage()));
				
		mockMvc.perform(post(PathConstants.Endpoints.EXPENSE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testExpenseRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.CONFLICT.value()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.DATABASE_ERROR.name()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(ErrorCode.DATABASE_ERROR.getMessage()))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(PathConstants.Error.Expense.URI))
				.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
}