package com.budgetmaster.application.controller;

import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.service.BudgetService;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.PathConstants;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
@Import(JacksonConfig.class)
@DisplayName("Budget Controller Tests")
class BudgetControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@SuppressWarnings("removal")
	@MockBean
    private BudgetService budgetService;

	private Budget testBudget;
	
	@BeforeEach
	void setUp() {
		testBudget = BudgetBuilder.defaultBudget().build();
	}

	@Nested
	@DisplayName("GET /budget Operations")
	class GetBudgetOperations {
		
		@Test
		@DisplayName("Should return budget when month is valid")
		void getBudget_withValidMonth_returnsOk() throws Exception {
			when(budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(testBudget);

			mockMvc.perform(get(PathConstants.Endpoints.BUDGET)
					.param(PathConstants.RequestParams.MONTH, BudgetConstants.Default.YEAR_MONTH.toString()))
					.andExpect(status().isOk())
					.andExpect(jsonPath(PathConstants.JsonProperties.Budget.TOTAL_INCOME).value(BudgetConstants.Default.TOTAL_INCOME))
					.andExpect(jsonPath(PathConstants.JsonProperties.Budget.TOTAL_EXPENSE).value(BudgetConstants.Default.TOTAL_EXPENSE))
					.andExpect(jsonPath(PathConstants.JsonProperties.Budget.SAVINGS).value(BudgetConstants.Default.SAVINGS))
					.andExpect(jsonPath(PathConstants.JsonProperties.Budget.CURRENCY).value(BudgetConstants.Default.CURRENCY.getCurrencyCode()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Budget.MONTH_YEAR).isArray())
					.andExpect(jsonPath(PathConstants.JsonProperties.Budget.YEAR).value(BudgetConstants.Default.YEAR))
					.andExpect(jsonPath(PathConstants.JsonProperties.Budget.MONTH).value(BudgetConstants.Default.MONTH));

			verify(budgetService).getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString());
		}

		@Test
		@DisplayName("Should return not found when month does not exist")
		void getBudget_withNonExistentMonth_returnsNotFound() throws Exception {
			when(budgetService.getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH.toString()))
					.thenThrow(new BudgetNotFoundException(String.format(ErrorConstants.Budget.NOT_FOUND_FOR_MONTH, BudgetConstants.NonExistent.YEAR_MONTH)));

			mockMvc.perform(get(PathConstants.Endpoints.BUDGET)
					.param(PathConstants.RequestParams.MONTH, BudgetConstants.NonExistent.YEAR_MONTH.toString()))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(String.format(ErrorConstants.Budget.NOT_FOUND_FOR_MONTH, BudgetConstants.NonExistent.YEAR_MONTH)))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(PathConstants.Error.Budget.URI))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());

			verify(budgetService).getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH.toString());
		}

		@Test
		@DisplayName("Should return internal server error when service error occurs")
		void getBudget_withServiceError_returnsInternalServerError() throws Exception {
			when(budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString()))
					.thenThrow(new RuntimeException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
					
			mockMvc.perform(get(PathConstants.Endpoints.BUDGET)
					.param(PathConstants.RequestParams.MONTH, BudgetConstants.Default.YEAR_MONTH.toString()))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.INTERNAL_SERVER_ERROR.name()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(PathConstants.Error.Budget.URI))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());

			verify(budgetService).getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString());
		}
	}

	@Nested
	@DisplayName("DELETE /budget/{id} Operations")
	class DeleteBudgetOperations {
		
		@Test
		@DisplayName("Should delete budget when ID is valid")
		void deleteBudget_withValidId_returnsNoContent() throws Exception {
			doNothing()
					.when(budgetService)
					.deleteBudget(BudgetConstants.Default.ID);
			
			mockMvc.perform(delete(PathConstants.Endpoints.BUDGET_WITH_ID, BudgetConstants.Default.ID))
					.andExpect(status().isNoContent());

			verify(budgetService).deleteBudget(BudgetConstants.Default.ID);
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void deleteBudget_withNonExistentId_returnsNotFound() throws Exception {
			doThrow(new BudgetNotFoundException(String.format(ErrorConstants.Budget.NOT_FOUND_WITH_ID, BudgetConstants.NonExistent.ID)))
					.when(budgetService)
					.deleteBudget(BudgetConstants.NonExistent.ID);

			mockMvc.perform(delete(PathConstants.Endpoints.BUDGET_WITH_ID, BudgetConstants.NonExistent.ID))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(String.format(ErrorConstants.Budget.NOT_FOUND_WITH_ID, BudgetConstants.NonExistent.ID)))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(String.format(PathConstants.Error.Budget.URI_WITH_ID, BudgetConstants.NonExistent.ID)))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());

			verify(budgetService).deleteBudget(BudgetConstants.NonExistent.ID);
		}
	}
}