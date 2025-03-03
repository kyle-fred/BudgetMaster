package com.budgetmaster.controller;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.enums.ExpenseType;
import com.budgetmaster.service.ExpenseService;
import com.budgetmaster.model.Expense;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
    private ExpenseService expenseService;
	
	@Test
	void shouldReturnExpenseWhenValidRequest() throws Exception {

		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setTarget("Estate Agent XYZ");
        request.setAmount(1000.0);
        request.setExpenseType(ExpenseType.RECURRING);
		
		Expense expectedExpense = new Expense();
        expectedExpense.setId(1L);
        expectedExpense.setName("Rent");
        expectedExpense.setTarget("Estate Agent XYZ");
        expectedExpense.setAmount(1000.0);
        expectedExpense.setExpenseType(ExpenseType.RECURRING);
		
		// Mock successful create
		Mockito.when(expenseService.createExpense(Mockito.any()))
			.thenReturn(expectedExpense);
		
		// POST to /api/expense & Assert OK response
		mockMvc.perform(post("/api/expense")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Rent"))
			.andExpect(jsonPath("$.target").value("Estate Agent XYZ"))
			.andExpect(jsonPath("$.amount").value(1000.0))
			.andExpect(jsonPath("$.expenseType").value("RECURRING"));
		
		Mockito.verify(expenseService, Mockito.times(1))
			.createExpense(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
        request.setTarget("Estate Agent XYZ");
        request.setAmount(1000.0);
        request.setExpenseType(ExpenseType.RECURRING);
		
	    // POST to /api/expense & Assert bad request
		mockMvc.perform(post("/api/expense")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Expense name is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
			.createExpense(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenTargetIsMissing() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
		request.setName("Rent");
        request.setAmount(1000.0);
        request.setExpenseType(ExpenseType.RECURRING);
		
	    // POST to /api/expense & Assert bad request
		mockMvc.perform(post("/api/expense")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.target").value("Expense target is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
			.createExpense(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenAmountIsMissing() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setTarget("Estate Agent XYZ");
        request.setExpenseType(ExpenseType.RECURRING);
		
	    // POST to /api/expense & Assert bad request
		mockMvc.perform(post("/api/expense")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.amount").value("Expense amount is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
			.createExpense(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenExpenseAmountIsNegative() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setTarget("Estate Agent XYZ");
        request.setAmount(-1000.0);
        request.setExpenseType(ExpenseType.RECURRING);
		
	    // POST to /api/expense & Assert bad request
		mockMvc.perform(post("/api/expense")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.amount").value("Expense amount cannot be negative."));
		
		Mockito.verify(expenseService, Mockito.times(0))
			.createExpense(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenExpenseTypeIsMissing() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setTarget("Estate Agent XYZ");
        request.setAmount(1000.0);
		
	    // POST to /api/expense & Assert bad request
		mockMvc.perform(post("/api/expense")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.expenseType").value("Expense type is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
			.createExpense(Mockito.any());
	}
	
	@Test
	void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setTarget("Estate Agent XYZ");
        request.setAmount(1000.0);
        request.setExpenseType(ExpenseType.RECURRING);

	    // Mock internal server error on create
	    Mockito.when(expenseService.createExpense(Mockito.any()))
	            .thenThrow(new RuntimeException("Service failure"));

	    // POST to /api/expense & Assert internal server error
	    mockMvc.perform(post("/api/expense")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$").value("An unexpected error occurred."));

	    Mockito.verify(expenseService, Mockito.times(1))
	    	.createExpense(Mockito.any());
	}
	
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {
		
	    ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setTarget("Estate Agent XYZ");
        request.setAmount(1000.0);
        request.setExpenseType(ExpenseType.RECURRING);

	    // Mock data integrity violation on create
	    Mockito.when(expenseService.createExpense(Mockito.any()))
	            .thenThrow(new DataIntegrityViolationException("Database constraint violation"));

	    // POST to /api/expense & Assert conflict status
	    mockMvc.perform(post("/api/expense")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict())
	            .andExpect(jsonPath("$").value("A database constraint was violated."));
	    
	    Mockito.verify(expenseService, Mockito.times(1))
	            .createExpense(Mockito.any());
	}
}