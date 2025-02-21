package com.budgetmaster.controller;

import com.budgetmaster.dto.BudgetRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
public class BudgetControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
    private BudgetService budgetService;
	
	@Test
	void shouldReturnBudgetWhenValidRequest() throws Exception {
		// Create a valid sample BudgetRequest
		BudgetRequest request = new BudgetRequest();
		request.setIncome(3000.0);
		request.setExpenses(1500.0);
		
		// Create expected response
		Budget expectedBudget = new Budget(3000, 1500);
		Mockito.when(budgetService.calculateandSaveBudget(Mockito.any()))
			.thenReturn(expectedBudget);
		
		// POST to /api/budget & Assert OK response
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.income").value(3000))
			.andExpect(jsonPath("$.expenses").value(1500))
			.andExpect(jsonPath("$.savings").value(1500));
		
		Mockito.verify(budgetService, Mockito.times(1))
			.calculateandSaveBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenIncomeIsMissing() throws Exception {
		// Create BudgetRequest with missing income
		BudgetRequest request = new BudgetRequest();
		request.setExpenses(1500.0);
		
	    // POST to /api/budget & Assert BadRequest
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.income").value("Income is required."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			.calculateandSaveBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenExpensesAreMissing() throws Exception {
		// Create BudgetRequest with missing expenses
		BudgetRequest request = new BudgetRequest();
		request.setIncome(1500.0);
		
	    // POST to /api/budget & Assert BadRequest
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.expenses").value("Expenses are required."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			.calculateandSaveBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenIncomeIsNegative() throws Exception {
		// Create BudgetRequest with negative income
		BudgetRequest request = new BudgetRequest();
		request.setIncome(-1500.0);
		request.setExpenses(1500.0);
		
		// POST to /api/budget & Assert BadRequest
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.income").value("Income cannot be negative."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			.calculateandSaveBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenExpensesAreNegative() throws Exception {
		// Create BudgetRequest with negative expenses
		BudgetRequest request = new BudgetRequest();
		request.setIncome(1500.0);
		request.setExpenses(-1500.0);
		
		// POST to /api/budget & Assert BadRequest
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.expenses").value("Expenses cannot be negative."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			.calculateandSaveBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
		// Create a valid sample BudgetRequest
	    BudgetRequest request = new BudgetRequest();
	    request.setIncome(3000.0);
	    request.setExpenses(1500.0);

	    // Simulate service failure
	    Mockito.when(budgetService.calculateandSaveBudget(Mockito.any()))
	            .thenThrow(new RuntimeException("Service failure"));

	    // POST to /api/budget & Assert Internal Server Error
	    mockMvc.perform(post("/api/budget")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$").value("An unexpected error occurred."));

	    Mockito.verify(budgetService, Mockito.times(1))
	    	.calculateandSaveBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {
	    // Create a valid sample BudgetRequest
	    BudgetRequest request = new BudgetRequest();
	    request.setIncome(3000.0);
	    request.setExpenses(1500.0);

	    // Simulate DataIntegrityViolationException
	    Mockito.when(budgetService.calculateandSaveBudget(Mockito.any()))
	            .thenThrow(new DataIntegrityViolationException("Database constraint violation"));

	    // POST to /api/budget & Assert Conflict status
	    mockMvc.perform(post("/api/budget")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict())
	            .andExpect(jsonPath("$").value("A database constraint was violated."));

	    Mockito.verify(budgetService, Mockito.times(1))
	            .calculateandSaveBudget(Mockito.any());
	}
}