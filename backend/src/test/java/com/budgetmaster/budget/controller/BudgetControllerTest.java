package com.budgetmaster.budget.controller;

import com.budgetmaster.application.controller.BudgetController;
import com.budgetmaster.application.service.BudgetService;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.common.enums.ErrorCode;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.testsupport.budget.builder.BudgetBuilder;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.constants.Error;
import com.budgetmaster.testsupport.constants.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
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
		testBudget = BudgetBuilder.defaultBudget().build();
	}

	@Test
	void getBudget_ValidMonth_ReturnsOk() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString()))
				.thenReturn(testBudget);

		mockMvc.perform(get(Paths.Endpoints.BUDGET)
				.param(Paths.RequestParams.MONTH, BudgetConstants.Default.YEAR_MONTH.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonProperties.Budget.ID).value(BudgetConstants.Default.ID))
				.andExpect(jsonPath(Paths.JsonProperties.Budget.TOTAL_INCOME).value(BudgetConstants.Default.TOTAL_INCOME))
				.andExpect(jsonPath(Paths.JsonProperties.Budget.TOTAL_EXPENSE).value(BudgetConstants.Default.TOTAL_EXPENSE))
				.andExpect(jsonPath(Paths.JsonProperties.Budget.SAVINGS).value(BudgetConstants.Default.SAVINGS))
				.andExpect(jsonPath(Paths.JsonProperties.Budget.CURRENCY).value(BudgetConstants.Default.CURRENCY.getCurrencyCode()))
				.andExpect(jsonPath(Paths.JsonProperties.Budget.MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonProperties.Budget.YEAR).value(BudgetConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonProperties.Budget.MONTH).value(BudgetConstants.Default.MONTH));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString());
	}

	@Test
	void getBudget_NonExistentMonth_ReturnsNotFound() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH.toString()))
				.thenThrow(new BudgetNotFoundException(String.format(Error.Budget.NOT_FOUND_FOR_MONTH, BudgetConstants.NonExistent.YEAR_MONTH)));

		mockMvc.perform(get(Paths.Endpoints.BUDGET)
				.param(Paths.RequestParams.MONTH, BudgetConstants.NonExistent.YEAR_MONTH.toString()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(Paths.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.MESSAGE).value(String.format(Error.Budget.NOT_FOUND_FOR_MONTH, BudgetConstants.NonExistent.YEAR_MONTH)))
				.andExpect(jsonPath(Paths.JsonProperties.Error.PATH).value(Paths.Error.Budget.URI))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERRORS).isEmpty());

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH.toString());
	}

	@Test
	void deleteBudget_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(budgetService)
				.deleteBudget(BudgetConstants.Default.ID);
		
		mockMvc.perform(delete(Paths.Endpoints.BUDGET_WITH_ID, BudgetConstants.Default.ID))
				.andExpect(status().isNoContent());

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(BudgetConstants.Default.ID);
	}

	@Test
	void deleteBudget_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new BudgetNotFoundException(String.format(Error.Budget.NOT_FOUND_WITH_ID, BudgetConstants.NonExistent.ID)))
				.when(budgetService)
				.deleteBudget(BudgetConstants.NonExistent.ID);

		mockMvc.perform(delete(Paths.Endpoints.BUDGET_WITH_ID, BudgetConstants.NonExistent.ID))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(Paths.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.MESSAGE).value(String.format(Error.Budget.NOT_FOUND_WITH_ID, BudgetConstants.NonExistent.ID)))
				.andExpect(jsonPath(Paths.JsonProperties.Error.PATH).value(String.format(Paths.Error.Budget.URI_WITH_ID, BudgetConstants.NonExistent.ID)))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERRORS).isEmpty());

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(BudgetConstants.NonExistent.ID);
	}

	@Test
	void getBudget_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString()))
				.thenThrow(new RuntimeException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
				
		mockMvc.perform(get(Paths.Endpoints.BUDGET)
				.param(Paths.RequestParams.MONTH, BudgetConstants.Default.YEAR_MONTH.toString()))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(Paths.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(Paths.JsonProperties.Error.STATUS).value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERROR_CODE).value(ErrorCode.INTERNAL_SERVER_ERROR.name()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.MESSAGE).value(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.PATH).value(Paths.Error.Budget.URI))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERRORS).isEmpty());

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH.toString());
	}
}