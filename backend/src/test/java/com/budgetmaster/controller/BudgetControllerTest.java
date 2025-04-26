package com.budgetmaster.controller;

import com.budgetmaster.enums.SupportedCurrency;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.service.BudgetService;
import com.budgetmaster.model.Budget;
import com.budgetmaster.test.constants.TestData.BudgetTestData;
import com.budgetmaster.test.constants.TestData.SharedTestData;
import com.budgetmaster.test.constants.TestMessages.BudgetErrorMessages;
import com.budgetmaster.test.constants.TestMessages.CommonErrorMessages;
import com.budgetmaster.test.constants.TestPaths.TestBudgetPaths;
import com.budgetmaster.test.constants.TestPaths.TestJsonPaths;
import com.budgetmaster.test.constants.TestPaths.TestRequestParams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

@WebMvcTest(BudgetController.class)
public class BudgetControllerTest {
	// -- Dependencies --
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private BudgetService budgetService;
	
	// -- Test Data --
	private static final Long testId = SharedTestData.TEST_EXISTING_ID;
	private static final BigDecimal testIncome = BudgetTestData.TEST_INCOME;
	private static final BigDecimal testExpense = BudgetTestData.TEST_EXPENSE;
	private static final BigDecimal testSavings = testIncome.subtract(testExpense);
	private static final Currency testCurrency = SupportedCurrency.GBP.getCurrency();
	private static final YearMonth testYearMonth = SharedTestData.TEST_EXISTING_MONTH_YEAR;
	private static final String testMonth = SharedTestData.TEST_EXISTING_MONTH_YEAR_STRING;

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

		mockMvc.perform(get(TestBudgetPaths.TEST_BUDGET_ENDPOINT)
				.param(TestRequestParams.TEST_PARAM_MONTH, testMonth))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_ID).value(testId))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_TOTAL_INCOME).value(testIncome))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_TOTAL_EXPENSE).value(testExpense))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_SAVINGS).value(testSavings))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_CURRENCY).value(testCurrency.getCurrencyCode()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONTH).value(testMonth));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonth);
	}

	@Test
	void getBudget_NonExistentMonth_ReturnsNotFound() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(testMonth))
				.thenThrow(new BudgetNotFoundException(String.format(BudgetErrorMessages.TEST_MESSAGE_BUDGET_NOT_FOUND_FOR_MONTH, testMonth)));

		mockMvc.perform(get(TestBudgetPaths.TEST_BUDGET_ENDPOINT)
				.param(TestRequestParams.TEST_PARAM_MONTH, testMonth))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_ERROR).value(String.format(BudgetErrorMessages.TEST_MESSAGE_BUDGET_NOT_FOUND_FOR_MONTH, testMonth)));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonth);
	}

	// -- Delete Budget Tests --

	@Test
	void deleteBudget_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(budgetService)
				.deleteBudget(testId);
		
		mockMvc.perform(delete(TestBudgetPaths.TEST_BUDGET_ENDPOINT_WITH_ID, testId))
				.andExpect(status().isNoContent());

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(testId);
	}

	@Test
	void deleteBudget_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new BudgetNotFoundException(String.format(BudgetErrorMessages.TEST_MESSAGE_BUDGET_NOT_FOUND_WITH_ID, testId)))
				.when(budgetService)
				.deleteBudget(testId);

		mockMvc.perform(delete(TestBudgetPaths.TEST_BUDGET_ENDPOINT_WITH_ID, testId))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_ERROR).value(String.format(BudgetErrorMessages.TEST_MESSAGE_BUDGET_NOT_FOUND_WITH_ID, testId)));

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(testId);
	}

	// -- Error Handling Tests --

	@Test
	void getBudget_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(testMonth))
				.thenThrow(new RuntimeException(CommonErrorMessages.TEST_MESSAGE_SERVICE_FAILURE));
				
		mockMvc.perform(get(TestBudgetPaths.TEST_BUDGET_ENDPOINT)
				.param(TestRequestParams.TEST_PARAM_MONTH, testMonth))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_EMPTY).value(CommonErrorMessages.TEST_MESSAGE_INTERNAL_SERVER_ERROR));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonth);
	}
}