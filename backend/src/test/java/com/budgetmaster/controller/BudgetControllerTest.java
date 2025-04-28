package com.budgetmaster.controller;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.model.Budget;
import com.budgetmaster.service.BudgetService;
import com.budgetmaster.test.constants.TestData;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestPaths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
public class BudgetControllerTest {
	// -- Dependencies --
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private BudgetService budgetService;
	
	// -- Test Data --
	private static final Long testId = TestData.CommonTestDataConstants.ID_EXISTING;
	private static final BigDecimal testIncome = TestData.BudgetTestDataConstants.INCOME_AMOUNT;
	private static final BigDecimal testExpense = TestData.BudgetTestDataConstants.EXPENSE_AMOUNT;
	private static final BigDecimal testSavings = TestData.BudgetTestDataConstants.SAVINGS_AMOUNT;
	private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
	private static final YearMonth testYearMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
	private static final String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;

	// -- Test Objects --
	private Budget budget;
	
	// -- Setup --
	@BeforeEach
	void setUp() {
		// Setup Budget
		budget = new Budget(testYearMonth);
		budget.setId(testId);
		budget.setTotalIncome(testIncome);
		budget.setTotalExpense(testExpense);
		budget.setSavings(testSavings);
		budget.setCurrency(testCurrency);
	}

	// -- Get Budget Tests --

	@Test
	void getBudget_ValidMonth_ReturnsOk() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(testMonth))
				.thenReturn(budget);

		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, testMonth))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ID).value(testId))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TOTAL_INCOME).value(testIncome))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TOTAL_EXPENSE).value(testExpense))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_SAVINGS).value(testSavings))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_CURRENCY).value(testCurrency.getCurrencyCode()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(testMonth));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonth);
	}

	@Test
	void getBudget_NonExistentMonth_ReturnsNotFound() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(testMonth))
				.thenThrow(new BudgetNotFoundException(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, testMonth)));

		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, testMonth))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, testMonth)));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonth);
	}

	// -- Delete Budget Tests --

	@Test
	void deleteBudget_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(budgetService)
				.deleteBudget(testId);
		
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET_WITH_ID, testId))
				.andExpect(status().isNoContent());

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(testId);
	}

	@Test
	void deleteBudget_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new BudgetNotFoundException(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, testId)))
				.when(budgetService)
				.deleteBudget(testId);

		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET_WITH_ID, testId))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, testId)));

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(testId);
	}

	// -- Error Handling Tests --

	@Test
	void getBudget_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(testMonth))
				.thenThrow(new RuntimeException(TestMessages.CommonErrorMessageConstants.SERVICE_FAILURE));
				
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_BUDGET)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, testMonth))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_EMPTY).value(TestMessages.CommonErrorMessageConstants.UNEXPECTED_ERROR));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonth);
	}
}