package com.budgetmaster.controller;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.service.BudgetService;
import com.budgetmaster.model.Budget;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;

@WebMvcTest(BudgetController.class)
public class BudgetControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
    private BudgetService budgetService;
	
	@Test
	void shouldCreateBudgetWhenValidRequest() throws Exception {
		double expectedIncome = 3000.0;
		double expectedExpense = 1500.0;
		double expectedSavings = expectedIncome - expectedExpense;
		
		BudgetRequest request = new BudgetRequest();
		request.setTotalIncome(expectedIncome);
		request.setTotalExpense(expectedExpense);
		request.setMonthYear("2000-01");
		
		YearMonth testYearMonth = YearMonth.of(2000, 1);
		
		Budget expectedBudget = new Budget(expectedIncome, expectedExpense, testYearMonth);
		expectedBudget.setSavings(expectedSavings);
		
		Mockito.when(budgetService.createBudget(Mockito.any(BudgetRequest.class)))
			    .thenReturn(expectedBudget);
		
		mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(expectedIncome))
                .andExpect(jsonPath("$.totalExpense").value(expectedExpense))
                .andExpect(jsonPath("$.savings").value(expectedSavings))
                .andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		Mockito.verify(budgetService, Mockito.times(1))
			    .createBudget(Mockito.any(BudgetRequest.class));
	}
	
	@Test
	void shouldCreateBudgetWhenMonthYearIsNotProvided() throws Exception {
		double expectedIncome = 3000.0;
		double expectedExpense = 1500.0;
		double expectedSavings = expectedIncome - expectedExpense;
		
		BudgetRequest request = new BudgetRequest();
		request.setTotalIncome(expectedIncome);
		request.setTotalExpense(expectedExpense);
		
		YearMonth defaultYearMonth = YearMonth.now();
		
		Budget expectedBudget = new Budget(expectedIncome, expectedExpense, defaultYearMonth);
		expectedBudget.setSavings(expectedSavings);
		
		Mockito.when(budgetService.createBudget(Mockito.any(BudgetRequest.class)))
			    .thenReturn(expectedBudget);
		
		mockMvc.perform(post("/api/budgets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalIncome").value(expectedIncome))
				.andExpect(jsonPath("$.totalExpense").value(expectedExpense))
				.andExpect(jsonPath("$.savings").value(expectedSavings))
				.andExpect(jsonPath("$.monthYear").value(defaultYearMonth.toString()));
		
		Mockito.verify(budgetService, Mockito.times(1))
			    .createBudget(Mockito.any(BudgetRequest.class));
	}
	
	@Test
	void shouldGetBudgetWhenValidMonthYear() throws Exception {
	    String testYearMonth = "2000-01";
	    double expectedIncome = 3000.0;
	    double expectedExpense = 1500.0;
	    double expectedSavings = expectedIncome - expectedExpense;
	    
	    Budget budget = new Budget(expectedIncome, expectedExpense, YearMonth.parse(testYearMonth));
	    budget.setSavings(expectedSavings);
	    
	    Mockito.when(budgetService.getBudgetByMonthYear(testYearMonth))
                .thenReturn(budget);
	    
	    mockMvc.perform(get("/api/budgets")
	            .param("monthYear", testYearMonth)
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.totalIncome").value(expectedIncome))
	            .andExpect(jsonPath("$.totalExpense").value(expectedExpense))
	            .andExpect(jsonPath("$.savings").value(expectedSavings))
	            .andExpect(jsonPath("$.monthYear").value(testYearMonth));
	    
	    Mockito.verify(budgetService, Mockito.times(1))
	            .getBudgetByMonthYear(testYearMonth);
	}
	
	@Test
	void shouldUpdateBudgetWhenValidId() throws Exception {
		Long budgetId = 1L;
		double expectedIncome = 4000.0;
		double expectedExpense = 2000.0;
		double expectedSavings = expectedIncome - expectedExpense;
		
	    BudgetRequest updateRequest = new BudgetRequest();
	    updateRequest.setTotalIncome(expectedIncome);
	    updateRequest.setTotalExpense(expectedExpense);
	    
	    Budget updatedBudget = new Budget(expectedIncome, expectedExpense, YearMonth.parse("2000-01"));
	    updatedBudget.setId(budgetId);
	    updatedBudget.setSavings(expectedSavings);
	    
	    Mockito.when(budgetService.updateBudget(Mockito.eq(budgetId), Mockito.any(BudgetRequest.class)))
	            .thenReturn(updatedBudget);
	    
	    mockMvc.perform(put("/api/budgets/{id}", budgetId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateRequest)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.totalIncome").value(expectedIncome))
	            .andExpect(jsonPath("$.totalExpense").value(expectedExpense))
	            .andExpect(jsonPath("$.savings").value(expectedSavings))
	            .andExpect(jsonPath("$.monthYear").value("2000-01"));
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
	void shouldReturnBadRequestWhenIncomeIsMissing() throws Exception {
		BudgetRequest request = new BudgetRequest();
		request.setTotalExpense(1500.0);
		
		mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.totalIncome").value("Total income is required."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			    .createBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenExpenseIsMissing() throws Exception {
		BudgetRequest request = new BudgetRequest();
		request.setTotalIncome(1500.0);
		
		mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.totalExpense").value("Total expense is required."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			    .createBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenIncomeIsNegative() throws Exception {
		BudgetRequest request = new BudgetRequest();
		request.setTotalIncome(-1500.0);
		request.setTotalExpense(1500.0);
		
		mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.totalIncome").value("Total income cannot be negative."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			    .createBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenExpenseIsNegative() throws Exception {
		BudgetRequest request = new BudgetRequest();
		request.setTotalIncome(1500.0);
		request.setTotalExpense(-1500.0);
		
		mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.totalExpense").value("Total expense cannot be negative."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			    .createBudget(Mockito.any());
	}
	
    @Test
    void shouldReturnBadRequestWhenMonthYearFormatIsInvalid() throws Exception {
        BudgetRequest request = new BudgetRequest();
        request.setTotalIncome(3000.0);
        request.setTotalExpense(1500.0);
        request.setMonthYear("2000/01");

        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.monthYear").value("Month year must be in format YYYY-MM"));

        Mockito.verify(budgetService, Mockito.times(0))
                .createBudget(Mockito.any());
    }
	
	@Test
	void shouldReturnNotFoundWhenBudgetIsNotFound() throws Exception {
		String testYearMonth = "2000-01";
		
		Mockito.when(budgetService.getBudgetByMonthYear(testYearMonth))
				.thenThrow(new BudgetNotFoundException("Budget not found for month: " + testYearMonth));
		
		mockMvc.perform(get("/api/budgets")
				.param("monthYear", testYearMonth)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Budget not found for month: " + testYearMonth));
		
		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonthYear(testYearMonth);
	}
	
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentBudget() throws Exception {
	    Long budgetId = 1L;
	    BudgetRequest request = new BudgetRequest();
	    request.setTotalIncome(3000.0);
	    request.setTotalExpense(1500.0);
	    
	    Mockito.when(budgetService.updateBudget(Mockito.eq(budgetId), Mockito.any(BudgetRequest.class)))
	            .thenThrow(new BudgetNotFoundException("Budget not found with id: " + budgetId));
	    
	    mockMvc.perform(put("/api/budgets/{id}", budgetId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isNotFound())
	            .andExpect(jsonPath("$.error").value("Budget not found with id: " + budgetId));
	    
	    Mockito.verify(budgetService, Mockito.times(1))
	            .updateBudget(Mockito.eq(budgetId), Mockito.any(BudgetRequest.class));
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
    
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {
	    BudgetRequest request = new BudgetRequest();
	    request.setTotalIncome(3000.0);
	    request.setTotalExpense(1500.0);

	    Mockito.when(budgetService.createBudget(Mockito.any()))
	            .thenThrow(new DataIntegrityViolationException("Database constraint violation"));

	    mockMvc.perform(post("/api/budgets")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict())
	            .andExpect(jsonPath("$").value("A database constraint was violated."));

	    Mockito.verify(budgetService, Mockito.times(1))
	            .createBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
	    BudgetRequest request = new BudgetRequest();
	    request.setTotalIncome(3000.0);
	    request.setTotalExpense(1500.0);

	    Mockito.when(budgetService.createBudget(Mockito.any()))
	            .thenThrow(new RuntimeException("Service failure"));

	    mockMvc.perform(post("/api/budgets")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$").value("An unexpected error occurred."));

	    Mockito.verify(budgetService, Mockito.times(1))
	    	.createBudget(Mockito.any());
	}
}