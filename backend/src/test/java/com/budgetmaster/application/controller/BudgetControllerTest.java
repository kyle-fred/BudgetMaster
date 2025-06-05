package com.budgetmaster.application.controller;

import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.service.BudgetService;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.assertions.controller.BudgetControllerAssertions;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
			
			ResultActions validGetRequest = mockMvc.perform(get(PathConstants.Endpoints.BUDGET)
					.param(PathConstants.RequestParams.MONTH, BudgetConstants.Default.YEAR_MONTH.toString()));

			BudgetControllerAssertions.assertThat(validGetRequest)
				.isDefaultBudgetResponse();

			verify(budgetService).getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString());
		}

		@Test
		@DisplayName("Should return not found when month does not exist")
		void getBudget_withNonExistentMonth_returnsNotFound() throws Exception {
			when(budgetService.getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH.toString()))
					.thenThrow(new BudgetNotFoundException(String.format(ErrorConstants.Budget.NOT_FOUND_FOR_MONTH, BudgetConstants.NonExistent.YEAR_MONTH)));
			
			ResultActions nonExistentMonthRequest = mockMvc.perform(get(PathConstants.Endpoints.BUDGET)
					.param(PathConstants.RequestParams.MONTH, BudgetConstants.NonExistent.YEAR_MONTH.toString()));

			BudgetControllerAssertions.assertThat(nonExistentMonthRequest)
				.isNotFoundForMonth(BudgetConstants.NonExistent.YEAR_MONTH);

			verify(budgetService).getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH.toString());
		}

		@Test
		@DisplayName("Should return internal server error when service error occurs")
		void getBudget_withServiceError_returnsInternalServerError() throws Exception {
			when(budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString()))
					.thenThrow(new RuntimeException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
			
			ResultActions serviceErrorRequest = mockMvc.perform(get(PathConstants.Endpoints.BUDGET)
					.param(PathConstants.RequestParams.MONTH, BudgetConstants.Default.YEAR_MONTH.toString()));

			BudgetControllerAssertions.assertThat(serviceErrorRequest)
				.isInternalServerError();

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
			
			ResultActions validDeleteRequest = mockMvc.perform(delete(PathConstants.Endpoints.BUDGET_WITH_ID, BudgetConstants.Default.ID));
			
			BudgetControllerAssertions.assertThat(validDeleteRequest)
				.isNoContent();

			verify(budgetService).deleteBudget(BudgetConstants.Default.ID);
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void deleteBudget_withNonExistentId_returnsNotFound() throws Exception {
			doThrow(new BudgetNotFoundException(String.format(ErrorConstants.Budget.NOT_FOUND_WITH_ID, BudgetConstants.NonExistent.ID)))
					.when(budgetService)
					.deleteBudget(BudgetConstants.NonExistent.ID);

			ResultActions nonExistentIdRequest = mockMvc.perform(delete(PathConstants.Endpoints.BUDGET_WITH_ID, BudgetConstants.NonExistent.ID));
			
			BudgetControllerAssertions.assertThat(nonExistentIdRequest)
				.isNotFoundForId(BudgetConstants.NonExistent.ID);

			verify(budgetService).deleteBudget(BudgetConstants.NonExistent.ID);
		}
	}
}