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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
		// Setup request with valid input
		BudgetRequest request = new BudgetRequest();
		request.setIncome(3000.0);
		request.setExpenses(1500.0);
		request.setMonthYear("2001-01");
		
		YearMonth testYearMonth = YearMonth.of(2000, 1);
		
		// Create expected Budget object
		Budget expectedBudget = new Budget(3000, 1500, testYearMonth);
		
		// Mock successful create
		Mockito.when(budgetService.createBudget(Mockito.any()))
			.thenReturn(expectedBudget);
		
		// POST to /api/budget & Assert OK response
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.income").value(3000))
			.andExpect(jsonPath("$.expenses").value(1500))
			.andExpect(jsonPath("$.savings").value(1500))
			.andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		// Verify the service call was made exactly once
		Mockito.verify(budgetService, Mockito.times(1))
			.createBudget(Mockito.any());
	}
	
	@Test
	void shouldCreateBudgetWhenMonthYearIsNotProvided() throws Exception {
		BudgetRequest request = new BudgetRequest();
		request.setIncome(3000.0);
		request.setExpenses(1500.0);
		request.setMonthYear(null);
		
		YearMonth defaultYearMonth = YearMonth.now();
		
		Budget expectedBudget = new Budget(3000, 1500, defaultYearMonth);
		
		Mockito.when(budgetService.createBudget(Mockito.any()))
			.thenReturn(expectedBudget);
		
		mockMvc.perform(post("/api/budget")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.income").value(3000))
				.andExpect(jsonPath("$.expenses").value(1500))
				.andExpect(jsonPath("$.savings").value(1500))
				.andExpect(jsonPath("$.monthYear").value(defaultYearMonth.toString()));
		
		Mockito.verify(budgetService, Mockito.times(1))
			.createBudget(Mockito.any());
	}

	
	@Test
	void shouldGetBudgetWhenValidMonthYear() throws Exception {
	    String testYearMonth = "2020-01";
	    
	    Budget budget = new Budget(3000.0, 1500.0, YearMonth.parse(testYearMonth));
	    
	    // Mock successful read
	    Mockito.when(budgetService.getBudgetByMonthYear(testYearMonth)).thenReturn(budget);
	    
	    // GET /api/budget & Assert OK response
	    mockMvc.perform(get("/api/budget/{monthYear}", testYearMonth)
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.income").value(3000.0))
	            .andExpect(jsonPath("$.expenses").value(1500.0))
	            .andExpect(jsonPath("$.savings").value(1500.0))
	            .andExpect(jsonPath("$.monthYear").value(testYearMonth));
	    
	    Mockito.verify(budgetService, Mockito.times(1))
	            .getBudgetByMonthYear(testYearMonth);
	}
	
	@Test
	void shouldGetCurrentMonthsBudgetWhenNotSpecified() throws Exception {
	    YearMonth currentMonth = YearMonth.now();
	    
	    Budget budget = new Budget(3000.0, 1500.0, currentMonth);
	    
	    // Mock successful read
	    Mockito.when(budgetService.getBudgetByMonthYear(null)).thenReturn(budget);
	    
	    // GET /api/budget & Assert OK response
	    mockMvc.perform(get("/api/budget")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.income").value(3000.0))
	            .andExpect(jsonPath("$.expenses").value(1500.0))
	            .andExpect(jsonPath("$.savings").value(1500.0))
	            .andExpect(jsonPath("$.monthYear").value(currentMonth.toString()));
	    
	    Mockito.verify(budgetService, Mockito.times(1))
	            .getBudgetByMonthYear(null);
	}
	
	@Test
	void shouldUpdateBudgetWhenValidMonthYear() throws Exception {
		String testYearMonth = "2020-01";
		
	    BudgetRequest updateRequest = new BudgetRequest();
	    updateRequest.setIncome(4000.0);
	    updateRequest.setExpenses(2000.0);
	    
	    Budget updatedBudget = new Budget(4000.0, 2000.0, YearMonth.parse(testYearMonth));
	    updatedBudget.setId(1L);
	    
	    // Mock successful update
	    Mockito.when(budgetService.updateBudget(Mockito.eq(testYearMonth), Mockito.any(BudgetRequest.class)))
	            .thenReturn(updatedBudget);
	    
	    // PUT /api/budget & Assert OK response
	    mockMvc.perform(put("/api/budget/{monthYear}", testYearMonth)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateRequest)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.income").value(4000.0))
	            .andExpect(jsonPath("$.expenses").value(2000.0))
	            .andExpect(jsonPath("$.savings").value(2000.0))
	            .andExpect(jsonPath("$.monthYear").value(testYearMonth));
	}
	
    @Test
    void shouldDeleteBudgetWhenValidMonthYear() throws Exception {
    	String testYearMonth = "2000-01";
    	
    	Mockito.doNothing().when(budgetService).deleteBudget(testYearMonth);
    	
        mockMvc.perform(delete("/api/budget/{monthYear}", testYearMonth))
                .andExpect(status().isNoContent());
        
        Mockito.verify(budgetService, Mockito.times(1)).deleteBudget(testYearMonth);
    }
	
	@Test
	void shouldReturnBadRequestWhenIncomeIsMissing() throws Exception {
		BudgetRequest request = new BudgetRequest();
		request.setExpenses(1500.0);
		
	    // POST to /api/budget & Assert bad request
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.income").value("Income is required."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			.createBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenExpensesAreMissing() throws Exception {
		BudgetRequest request = new BudgetRequest();
		request.setIncome(1500.0);
		
	    // POST to /api/budget & Assert bad request
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.expenses").value("Expenses are required."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			.createBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenIncomeIsNegative() throws Exception {
		BudgetRequest request = new BudgetRequest();
		request.setIncome(-1500.0);
		request.setExpenses(1500.0);
		
		// POST to /api/budget & Assert bad request
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.income").value("Income cannot be negative."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			.createBudget(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenExpensesAreNegative() throws Exception {
		BudgetRequest request = new BudgetRequest();
		request.setIncome(1500.0);
		request.setExpenses(-1500.0);
		
		// POST to /api/budget & Assert bad request
		mockMvc.perform(post("/api/budget")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.expenses").value("Expenses cannot be negative."));
		
		Mockito.verify(budgetService, Mockito.times(0))
			.createBudget(Mockito.any());
	}
	
    @Test
    void shouldReturnBadRequestWhenMonthYearFormatIsInvalid() throws Exception {
        BudgetRequest request = new BudgetRequest();
        request.setIncome(3000.0);
        request.setExpenses(1500.0);
        request.setMonthYear("2000/01");

        mockMvc.perform(post("/api/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.monthYear").value("Invalid monthYear value. Expected format: YYYY-MM."));

        Mockito.verify(budgetService, Mockito.times(0)).createBudget(Mockito.any());
    }
	
	@Test
	void shouldReturnNotFoundWhenBudgetIsNotFound() throws Exception {
		String testYearMonth = "2000-01";
		
		// Mock failed read
		Mockito.when(budgetService.getBudgetByMonthYear(testYearMonth))
			.thenThrow(new BudgetNotFoundException("Budget not found for month: " + testYearMonth));
		
		// GET /api/budget/{monthYear} & expect 404
		mockMvc.perform(get("/api/budget/{monthYear}", testYearMonth)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Budget not found for month: " + testYearMonth));
		
		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetByMonthYear(testYearMonth);
	}
	
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentBudget() throws Exception {
	    String testYearMonth = "2000-01";
	    BudgetRequest request = new BudgetRequest();
	    request.setIncome(3000.0);
	    request.setExpenses(1500.0);
	    
	    // Mock failed update
	    Mockito.when(budgetService.updateBudget(Mockito.eq(testYearMonth), Mockito.any(BudgetRequest.class)))
	            .thenThrow(new BudgetNotFoundException("Budget not found for month: " + testYearMonth));
	    
	    // PUT /api/budget/{monthYear} & expect 404
	    mockMvc.perform(put("/api/budget/{monthYear}", testYearMonth)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isNotFound())
	            .andExpect(jsonPath("$.error").value("Budget not found for month: " + testYearMonth));
	    
	    Mockito.verify(budgetService, Mockito.times(1))
	            .updateBudget(Mockito.eq(testYearMonth), Mockito.any(BudgetRequest.class));
    }
    
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentBudget() throws Exception {
    	String testYearMonth = "2000-01";
    	
        Mockito.doThrow(new BudgetNotFoundException("Budget not found for month: " + testYearMonth))
        		.when(budgetService).deleteBudget(Mockito.eq(testYearMonth));
    	
    	// DELETE /api/budget & Assert not found response
        mockMvc.perform(delete("/api/budget/{monthYear}", testYearMonth))
                .andExpect(status().isNotFound());
        
        Mockito.verify(budgetService, Mockito.times(1)).deleteBudget(testYearMonth);
    }
    
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {
		
	    BudgetRequest request = new BudgetRequest();
	    request.setIncome(3000.0);
	    request.setExpenses(1500.0);

	    // Mock data integrity violation on create
	    Mockito.when(budgetService.createBudget(Mockito.any()))
	            .thenThrow(new DataIntegrityViolationException("Database constraint violation"));

	    // POST to /api/budget & Assert conflict status
	    mockMvc.perform(post("/api/budget")
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
	    request.setIncome(3000.0);
	    request.setExpenses(1500.0);

	    // Mock internal server error on create
	    Mockito.when(budgetService.createBudget(Mockito.any()))
	            .thenThrow(new RuntimeException("Service failure"));

	    // POST to /api/budget & Assert internal server error
	    mockMvc.perform(post("/api/budget")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$").value("An unexpected error occurred."));

	    Mockito.verify(budgetService, Mockito.times(1))
	    	.createBudget(Mockito.any());
	}
}