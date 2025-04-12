package com.budgetmaster.controller;

import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.service.BudgetService;
import com.budgetmaster.model.Budget;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;

@WebMvcTest(BudgetController.class)
public class BudgetControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private BudgetService budgetService;
	
	@Test
	void shouldGetBudgetWhenValidMonth() throws Exception {
	    String testMonthString = "2000-01";
	    double expectedIncome = 3000.0;
	    double expectedExpense = 1500.0;
	    double expectedSavings = expectedIncome - expectedExpense;
	    
	    Budget budget = new Budget(YearMonth.parse(testMonthString));
	    budget.setTotalIncome(expectedIncome);
	    budget.setTotalExpense(expectedExpense);
	    budget.setSavings(expectedSavings);
	    
	    Mockito.when(budgetService.getBudgetByMonth(testMonthString))
                .thenReturn(budget);
	    
	    mockMvc.perform(get("/api/budgets")
	            .param("month", testMonthString)
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.totalIncome").value(expectedIncome))
	            .andExpect(jsonPath("$.totalExpense").value(expectedExpense))
	            .andExpect(jsonPath("$.savings").value(expectedSavings))
	            .andExpect(jsonPath("$.month").value(testMonthString));
	    
	    Mockito.verify(budgetService, Mockito.times(1))
	            .getBudgetByMonth(testMonthString);
	}
	
    @Test
    void shouldDeleteBudgetWhenValidId() throws Exception {
    	Long budgetId = 1L;
    	
    	Mockito.doNothing()
                .when(budgetService)
                .deleteBudget(budgetId);
    	
        mockMvc.perform(delete("/api/budgets/{id}", budgetId))
                .andExpect(status().isNoContent());
        
        Mockito.verify(budgetService, Mockito.times(1))
                .deleteBudget(budgetId);
    }
	
	@Test
	void shouldReturnNotFoundWhenBudgetIsNotFound() throws Exception {
		String testMonthString = "2000-01";
		
		Mockito.when(budgetService.getBudgetByMonth(testMonthString))
				.thenThrow(new BudgetNotFoundException("Budget not found for month: " + testMonthString));
		
		mockMvc.perform(get("/api/budgets")
				.param("month", testMonthString)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Budget not found for month: " + testMonthString));
		
		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonth(testMonthString);
	}
    
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentBudget() throws Exception {
    	Long budgetId = 1L;
    	
        Mockito.doThrow(new BudgetNotFoundException("Budget not found with id: " + budgetId))
        		.when(budgetService).deleteBudget(budgetId);
    	
        mockMvc.perform(delete("/api/budgets/{id}", budgetId))
                .andExpect(status().isNotFound());
        
        Mockito.verify(budgetService, Mockito.times(1))
                .deleteBudget(budgetId);
    }
}