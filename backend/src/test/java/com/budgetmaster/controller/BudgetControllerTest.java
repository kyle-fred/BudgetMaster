package com.budgetmaster.controller;

import com.budgetmaster.enums.SupportedCurrency;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.service.BudgetService;
import com.budgetmaster.model.Budget;

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
	private static final Long testId = 1L;
	private static final BigDecimal testIncome = new BigDecimal("543.21");
	private static final BigDecimal testExpense = new BigDecimal("123.45");
	private static final BigDecimal testSavings = testIncome.subtract(testExpense);
	private static final Currency testCurrency = SupportedCurrency.GBP.getCurrency();
	private static final YearMonth testYearMonth = YearMonth.of(2000, 1);
	private static final String testMonth = "2000-01";
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

		mockMvc.perform(get("/api/budgets")
				.param("month", testMonth))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(testId))
				.andExpect(jsonPath("$.total-income").value(testIncome))
				.andExpect(jsonPath("$.total-expense").value(testExpense))
				.andExpect(jsonPath("$.savings").value(testSavings))
				.andExpect(jsonPath("$.currency").value(testCurrency.getCurrencyCode()))
				.andExpect(jsonPath("$.month").value(testMonth));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonth);
	}

	@Test
	void getBudget_NonExistentMonth_ReturnsNotFound() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(testMonth))
				.thenThrow(new BudgetNotFoundException("Budget not found for month: " + testMonth));

		mockMvc.perform(get("/api/budgets")
				.param("month", testMonth))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Budget not found for month: " + testMonth));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonth);
	}

	// -- Delete Budget Tests --

	@Test
	void deleteBudget_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(budgetService)
				.deleteBudget(testId);
		
		mockMvc.perform(delete("/api/budgets/{id}", testId))
				.andExpect(status().isNoContent());

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(testId);
	}

	@Test
	void deleteBudget_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new BudgetNotFoundException("Budget not found with id: " + testId))
				.when(budgetService)
				.deleteBudget(testId);

		mockMvc.perform(delete("/api/budgets/{id}", testId))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Budget not found with id: " + testId));

		Mockito.verify(budgetService, Mockito.times(1))
				.deleteBudget(testId);
	}

	// -- Error Handling Tests --

	@Test
	void getBudget_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(budgetService.getBudgetByMonth(testMonth))
				.thenThrow(new RuntimeException("Service failure"));
				
		mockMvc.perform(get("/api/budgets")
				.param("month", testMonth))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$").value("An unexpected error occurred."));

		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonth);
	}
}