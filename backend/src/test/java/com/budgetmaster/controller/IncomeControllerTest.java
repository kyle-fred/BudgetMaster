package com.budgetmaster.controller;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.service.IncomeService;
import com.budgetmaster.model.Income;
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

@WebMvcTest(IncomeController.class)
public class IncomeControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
    private IncomeService incomeService;
	
	@Test
	void shouldReturnIncomeWhenValidRequest() throws Exception {

		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
		
		Income expectedIncome = new Income();
        expectedIncome.setId(1L);
        expectedIncome.setName("Salary");
        expectedIncome.setSource("Company XYZ");
        expectedIncome.setAmount(2000.0);
        expectedIncome.setType(TransactionType.RECURRING);
		
		// Mock successful create
		Mockito.when(incomeService.createIncome(Mockito.any()))
			.thenReturn(expectedIncome);
		
		// POST to /api/income & Assert OK response
		mockMvc.perform(post("/api/income")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Salary"))
			.andExpect(jsonPath("$.source").value("Company XYZ"))
			.andExpect(jsonPath("$.amount").value(2000.0))
			.andExpect(jsonPath("$.type").value("RECURRING"));
		
		Mockito.verify(incomeService, Mockito.times(1))
			.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
		
		IncomeRequest request = new IncomeRequest();
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
		
	    // POST to /api/income & Assert bad request
		mockMvc.perform(post("/api/income")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Income name is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
			.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenSourceIsMissing() throws Exception {
		
		IncomeRequest request = new IncomeRequest();
		request.setName("Salary");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
		
	    // POST to /api/income & Assert bad request
		mockMvc.perform(post("/api/income")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.source").value("Income source is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
			.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenAmountIsMissing() throws Exception {
		
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setType(TransactionType.RECURRING);
		
	    // POST to /api/income & Assert bad request
		mockMvc.perform(post("/api/income")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.amount").value("Income amount is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
			.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenIncomeAmountIsNegative() throws Exception {
		
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(-2000.0);
        request.setType(TransactionType.RECURRING);
		
	    // POST to /api/income & Assert bad request
		mockMvc.perform(post("/api/income")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.amount").value("Income amount cannot be negative."));
		
		Mockito.verify(incomeService, Mockito.times(0))
			.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenTransactionTypeIsMissing() throws Exception {
		
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
		
	    // POST to /api/income & Assert bad request
		mockMvc.perform(post("/api/income")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("Income transaction type is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
			.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
		
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);

	    // Mock internal server error on create
	    Mockito.when(incomeService.createIncome(Mockito.any()))
	            .thenThrow(new RuntimeException("Service failure"));

	    // POST to /api/income & Assert internal server error
	    mockMvc.perform(post("/api/income")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$").value("An unexpected error occurred."));

	    Mockito.verify(incomeService, Mockito.times(1))
	    	.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {
		
	    IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);

	    // Mock data integrity violation on create
	    Mockito.when(incomeService.createIncome(Mockito.any()))
	            .thenThrow(new DataIntegrityViolationException("Database constraint violation"));

	    // POST to /api/income & Assert conflict status
	    mockMvc.perform(post("/api/income")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict())
	            .andExpect(jsonPath("$").value("A database constraint was violated."));
	    
	    Mockito.verify(incomeService, Mockito.times(1))
	            .createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnIncomeWhenValidIdGet() throws Exception {
		
		Income income = new Income();
		income.setName("Salary");
        income.setSource("Company XYZ");
        income.setAmount(2000.0);
        income.setType(TransactionType.RECURRING);
		
		// Mock successful read
		Mockito.when(incomeService.getIncomeById(1L)).thenReturn(Optional.of(income));
		
		// GET /api/income & Assert OK response
		mockMvc.perform(get("/api/income/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Salary"))
				.andExpect(jsonPath("$.source").value("Company XYZ"))
				.andExpect(jsonPath("$.amount").value(2000.0))
				.andExpect(jsonPath("$.type").value("RECURRING"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(1L);
	}
	
	@Test
	void shouldReturnNotFoundWhenInvalidId() throws Exception {
		// Mock failed read
		Mockito.when(incomeService.getIncomeById(99L)).thenReturn(Optional.empty());
		
		// GET /api/income & Assert not found response
		mockMvc.perform(get("/api/income/99")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(99L);
	}
	
	@Test
	void shouldUpdateIncomeWhenValidId() throws Exception {
		
		Income existingIncome= new Income();
		existingIncome.setId(1L);
		existingIncome.setName("Salary");
		existingIncome.setSource("Company XYZ");
		existingIncome.setAmount(2000.0);
		existingIncome.setType(TransactionType.RECURRING);
		
		Income updatedIncome = new Income ();
		updatedIncome.setId(1L);
		updatedIncome.setName("Interest Income");
		updatedIncome.setSource("Bank XYZ");
		updatedIncome.setAmount(100.0);
		updatedIncome.setType(TransactionType.ONE_TIME);
		
		IncomeRequest updateRequest = new IncomeRequest();
		updateRequest.setName("Interest Income");
		updateRequest.setSource("Bank XYZ");
		updateRequest.setAmount(100.0);
		updateRequest.setType(TransactionType.ONE_TIME);
		
		// Mock successful update
		Mockito.when(incomeService.updateIncome(Mockito.eq(1L), Mockito.any(IncomeRequest.class)))
				.thenReturn(Optional.of(updatedIncome));
		
		// PUT /api/income & Assert OK response
		mockMvc.perform(put("/api/income/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Interest Income"))
				.andExpect(jsonPath("$.source").value("Bank XYZ"))
				.andExpect(jsonPath("$.amount").value(100.0))
				.andExpect(jsonPath("$.type").value("ONE_TIME"));
	}
	
    @Test
    void shouldReturnNotFoundWhenInvalidIdPut() throws Exception {
    	
        IncomeRequest updateRequest = new IncomeRequest();
		updateRequest.setName("Interest Income");
		updateRequest.setSource("Bank XYZ");
		updateRequest.setAmount(100.0);
		updateRequest.setType(TransactionType.ONE_TIME);
        
        // Mock failed update
        Mockito.when(incomeService.updateIncome(99L, updateRequest)).thenReturn(Optional.empty());
        
        // PUT /api/income & Assert not found response
        mockMvc.perform(put("/api/income/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldDeleteIncomeWhenValidId() throws Exception {
    	// Mock successful deletion
    	Mockito.when(incomeService.deleteIncome(1L)).thenReturn(true);
    	
    	// DELETE /api/income & Assert OK response
        mockMvc.perform(delete("/api/income/1"))
                .andExpect(status().isNoContent());
        
        Mockito.verify(incomeService, Mockito.times(1)).deleteIncome(1L);
    }
    
    @Test
    void shouldReturnNotFoundWhenInvalidIdDelete() throws Exception {
    	// Mock failed deletion (ID does not exist)
    	Mockito.when(incomeService.deleteIncome(1L)).thenReturn(false);
    	
    	// DELETE /api/income & Assert not found response
        mockMvc.perform(delete("/api/income/1"))
                .andExpect(status().isNotFound());
        
        Mockito.verify(incomeService, Mockito.times(1)).deleteIncome(1L);
    }
}