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
		// Create sample BudgetRequest
		BudgetRequest request = new BudgetRequest();
		request.setIncome(3000);
		request.setExpenses(1500);
		
		// Create expected response
		Budget expectedBudget = new Budget(3000, 1500);
		Mockito.when(budgetService.calculateBudget(Mockito.any()))
			.thenReturn(expectedBudget);
		
		// POST to /api/budget & Assert
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.income").value(3000))
			.andExpect(jsonPath("$.expenses").value(1500))
			.andExpect(jsonPath("$.savings").value(1500));
		
		Mockito.verify(budgetService, Mockito.times(1))
			.calculateBudget(Mockito.any());
	}
}