package com.budgetmaster.budget.controller;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.service.BudgetService;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.test.builder.BudgetTestBuilder;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestPaths;
import com.budgetmaster.test.constants.TestData.BudgetTestConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
@Import(JacksonConfig.class)
public class BudgetControllerTest {
	// -- Dependencies --
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private BudgetService budgetService;

	// -- Test Objects --
	private Budget testBudget;
	
	@BeforeEach
	void setUp() {
		testBudget = BudgetTestBuilder.defaultBudget().build();
	}

	@Test
	void getBudget_ValidMonth_ReturnsOk() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(BudgetTestConstants.Default.YEAR_MONTH.toString()))
				.thenReturn(testBudget);

		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, BudgetTestConstants.Default.YEAR_MONTH.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ID).value(BudgetTestConstants.Default.ID))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TOTAL_INCOME).value(BudgetTestConstants.Default.TOTAL_INCOME))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TOTAL_EXPENSE).value(BudgetTestConstants.Default.TOTAL_EXPENSE))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_SAVINGS).value(BudgetTestConstants.Default.SAVINGS))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_CURRENCY).value(BudgetTestConstants.Default.CURRENCY.getCurrencyCode()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_YEAR).value(BudgetTestConstants.Default.YEAR))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(BudgetTestConstants.Default.MONTH));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(BudgetTestConstants.Default.YEAR_MONTH.toString());
	}

	@Test
	void getBudget_NonExistentMonth_ReturnsNotFound() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(BudgetTestConstants.NonExistent.YEAR_MONTH.toString()))
				.thenThrow(new BudgetNotFoundException(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, BudgetTestConstants.NonExistent.YEAR_MONTH)));

		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, BudgetTestConstants.NonExistent.YEAR_MONTH.toString()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, BudgetTestConstants.NonExistent.YEAR_MONTH)));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(BudgetTestConstants.NonExistent.YEAR_MONTH.toString());
	}

	@Test
	void deleteBudget_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(budgetService)
				.deleteBudget(BudgetTestConstants.Default.ID);
		
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET_WITH_ID, BudgetTestConstants.Default.ID))
				.andExpect(status().isNoContent());

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(BudgetTestConstants.Default.ID);
	}

	@Test
	void deleteBudget_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new BudgetNotFoundException(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, BudgetTestConstants.NonExistent.ID)))
				.when(budgetService)
				.deleteBudget(BudgetTestConstants.NonExistent.ID);

		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET_WITH_ID, BudgetTestConstants.NonExistent.ID))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, BudgetTestConstants.NonExistent.ID)));

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(BudgetTestConstants.NonExistent.ID);
	}

	@Test
	void getBudget_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(BudgetTestConstants.Default.YEAR_MONTH.toString()))
				.thenThrow(new RuntimeException(TestMessages.CommonErrorMessageConstants.SERVICE_FAILURE));
				
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, BudgetTestConstants.Default.YEAR_MONTH.toString()))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_EMPTY).value(TestMessages.CommonErrorMessageConstants.UNEXPECTED_ERROR));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(BudgetTestConstants.Default.YEAR_MONTH.toString());
	}
}