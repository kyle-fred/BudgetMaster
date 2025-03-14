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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

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
		request.setMonthYear("2023-05");
		
		YearMonth testYearMonth = YearMonth.of(2023, 5);
		LocalDateTime now = LocalDateTime.now();
		
		// Create expected Budget object
		Budget expectedBudget = new Budget(3000, 1500, testYearMonth);
		expectedBudget.setCreatedAt(now);
		expectedBudget.setLastUpdatedAt(now);
		
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
			.andExpect(jsonPath("$.monthYear").value("2023-05"))
			.andExpect(jsonPath("$.createdAt").value(now.toString()))
			.andExpect(jsonPath("$.lastUpdatedAt").value(now.toString()));
		
		// Verify the service call was made exactly once
		Mockito.verify(budgetService, Mockito.times(1))
			.createBudget(Mockito.any());
	}

	
	@Test
	void shouldGetBudgetWhenValidId() throws Exception {
	    
	    YearMonth testYearMonth = YearMonth.of(2023, 5);
	    Budget budget = new Budget(3000.0, 1500.0, testYearMonth);
	    budget.setId(1L);
	    budget.setCreatedAt(LocalDateTime.now());
	    budget.setLastUpdatedAt(LocalDateTime.now());
	    
	    // Mock successful read
	    Mockito.when(budgetService.getBudgetById(1L)).thenReturn(Optional.of(budget));
	    
	    // GET /api/budget & Assert OK response
	    mockMvc.perform(get("/api/budget/1")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.id").value(1))
	            .andExpect(jsonPath("$.income").value(3000.0))
	            .andExpect(jsonPath("$.expenses").value(1500.0))
	            .andExpect(jsonPath("$.savings").value(1500.0))
	            .andExpect(jsonPath("$.monthYear").value("2023-05"))
	            .andExpect(jsonPath("$.createdAt").exists())
	            .andExpect(jsonPath("$.lastUpdatedAt").exists());
	    
	    Mockito.verify(budgetService, Mockito.times(1))
	            .getBudgetById(1L);
	}

	
	@Test
	void shouldUpdateBudgetWhenValidId() throws Exception {
	    
	    YearMonth testYearMonth = YearMonth.of(2023, 5);
	    Budget existingBudget = new Budget(3000.0, 1500.0, testYearMonth);
	    existingBudget.setId(1L);
	    existingBudget.setMonthYear(testYearMonth);
	    existingBudget.setCreatedAt(LocalDateTime.now());
	    existingBudget.setLastUpdatedAt(LocalDateTime.now());
	    
	    YearMonth updatedYearMonth = YearMonth.of(2023, 6);
	    Budget updatedBudget = new Budget(4000.0, 2000.0, testYearMonth);
	    updatedBudget.setId(1L);
	    updatedBudget.setSavings(2000.0);
	    updatedBudget.setMonthYear(updatedYearMonth);
	    updatedBudget.setCreatedAt(LocalDateTime.now());
	    updatedBudget.setLastUpdatedAt(LocalDateTime.now());
	    
	    BudgetRequest updateRequest = new BudgetRequest();
	    updateRequest.setIncome(4000.0);
	    updateRequest.setExpenses(2000.0);
	    updateRequest.setMonthYear("2023-06");
	    
	    // Mock successful update
	    Mockito.when(budgetService.updateBudget(Mockito.eq(1L), Mockito.any(BudgetRequest.class)))
	            .thenReturn(Optional.of(updatedBudget));
	    
	    // PUT /api/budget & Assert OK response
	    mockMvc.perform(put("/api/budget/1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateRequest)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.id").value(1))
	            .andExpect(jsonPath("$.income").value(4000.0))
	            .andExpect(jsonPath("$.expenses").value(2000.0))
	            .andExpect(jsonPath("$.savings").value(2000.0))
	            .andExpect(jsonPath("$.monthYear").value("2023-06"))
	            .andExpect(jsonPath("$.createdAt").exists())
	            .andExpect(jsonPath("$.lastUpdatedAt").exists());
	}

	
    @Test
    void shouldDeleteBudgetWhenValidId() throws Exception {
    	
    	// Mock successful deletion
    	Mockito.when(budgetService.deleteBudget(1L)).thenReturn(true);
    	
    	// DELETE /api/budget & Assert OK response
        mockMvc.perform(delete("/api/budget/1"))
                .andExpect(status().isNoContent());
        
        Mockito.verify(budgetService, Mockito.times(1)).deleteBudget(1L);
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
	void shouldReturnNotFoundWhenBudgetDoesNotExist() throws Exception {
		
		// Mock failed read
		Mockito.when(budgetService.getBudgetById(99L)).thenReturn(Optional.empty());
		
		// GET /api/budget & Assert not found response
		mockMvc.perform(get("/api/budget/99")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		Mockito.verify(budgetService, Mockito.times(1))
				.getBudgetById(99L);
	}
	
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentBudget() throws Exception {
    	
        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setIncome(3000.0);
        updateRequest.setExpenses(1500.0);
        
        // Mock failed update
        Mockito.when(budgetService.updateBudget(99L, updateRequest)).thenReturn(Optional.empty());
        
        // PUT /api/budget & Assert not found response
        mockMvc.perform(put("/api/budget/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentBudget() throws Exception {
    	
    	// Mock failed deletion (ID does not exist)
    	Mockito.when(budgetService.deleteBudget(1L)).thenReturn(false);
    	
    	// DELETE /api/budget & Assert not found response
        mockMvc.perform(delete("/api/budget/1"))
                .andExpect(status().isNotFound());
        
        Mockito.verify(budgetService, Mockito.times(1)).deleteBudget(1L);
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