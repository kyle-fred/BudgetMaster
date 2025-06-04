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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("Expense Controller Tests")
class ExpenseControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@SuppressWarnings("removal")
	@MockBean
    private ExpenseService expenseService;

	private Expense testExpense;
	private ExpenseRequest testExpenseRequest;

	@BeforeEach
	void setUp() {
		testExpense = ExpenseBuilder.defaultExpense().build();
		testExpenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();
	}

	@Nested
	@DisplayName("POST /expense Operations")
	class CreateExpenseOperations {
		
		@Test
		@DisplayName("Should create expense when request is valid")
		void createExpense_withValidRequest_returnsCreated() throws Exception {
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
					
			Mockito.verify(expenseService).createExpense(Mockito.any(ExpenseRequest.class));
		}

		@Test
		@DisplayName("Should return internal server error when service error occurs")
		void createExpense_withServiceError_returnsInternalServerError() throws Exception {
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
					
			Mockito.verify(expenseService).createExpense(Mockito.any(ExpenseRequest.class));
		}

		@Test
		@DisplayName("Should return conflict when data integrity violation occurs")
		void createExpense_withDataIntegrityViolation_returnsConflict() throws Exception {
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
					
			Mockito.verify(expenseService).createExpense(Mockito.any(ExpenseRequest.class));
		}
	}

	@Nested
	@DisplayName("GET /expense Operations")
	class GetExpenseOperations {
		
		@Test
		@DisplayName("Should return expense when ID is valid")
		void getExpense_withValidId_returnsOk() throws Exception {
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
					
			Mockito.verify(expenseService).getExpenseById(ExpenseConstants.Default.ID);
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void getExpense_withNonExistentId_returnsNotFound() throws Exception {
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
					
			Mockito.verify(expenseService).getExpenseById(ExpenseConstants.NonExistent.ID);
		}

		@Test
		@DisplayName("Should return all expenses when month is valid")
		void getAllExpenses_withValidMonth_returnsOk() throws Exception {
			List<Expense> expenseList = List.of(testExpense, testExpense);
			
			Mockito.when(expenseService.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(expenseList);
					
			mockMvc.perform(get(PathConstants.Endpoints.EXPENSE)
					.param(PathConstants.RequestParams.MONTH, ExpenseConstants.Default.YEAR_MONTH.toString())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath(PathConstants.JsonProperties.Expense.FIRST_NAME).value(ExpenseConstants.Default.NAME))
					.andExpect(jsonPath(PathConstants.JsonProperties.Expense.SECOND_NAME).value(ExpenseConstants.Default.NAME));
					
			Mockito.verify(expenseService).getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString());
		}
	}

	@Nested
	@DisplayName("PUT /expense/{id} Operations")
	class UpdateExpenseOperations {
		
		@Test
		@DisplayName("Should update expense when request is valid")
		void updateExpense_withValidRequest_returnsOk() throws Exception {
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
					
			Mockito.verify(expenseService).updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void updateExpense_withNonExistentId_returnsNotFound() throws Exception {
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
					
			Mockito.verify(expenseService).updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
		}
	}

	@Nested
	@DisplayName("DELETE /expense/{id} Operations")
	class DeleteExpenseOperations {
		
		@Test
		@DisplayName("Should delete expense when ID is valid")
		void deleteExpense_withValidId_returnsNoContent() throws Exception {
			Mockito.doNothing()
					.when(expenseService)
					.deleteExpense(ExpenseConstants.Default.ID);
					
			mockMvc.perform(delete(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.Default.ID))
					.andExpect(status().isNoContent());
					
			Mockito.verify(expenseService).deleteExpense(ExpenseConstants.Default.ID);
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void deleteExpense_withNonExistentId_returnsNotFound() throws Exception {
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
					
			Mockito.verify(expenseService).deleteExpense(ExpenseConstants.NonExistent.ID);
		}
	}
}