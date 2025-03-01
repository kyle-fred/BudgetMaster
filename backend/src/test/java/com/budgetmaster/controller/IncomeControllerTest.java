package com.budgetmaster.controller;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.enums.IncomeType;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        request.setIncomeType(IncomeType.RECURRING);
		
		Income expectedIncome = new Income();
        expectedIncome.setId(1L);
        expectedIncome.setName("Salary");
        expectedIncome.setSource("Company XYZ");
        expectedIncome.setAmount(2000.0);
        expectedIncome.setIncomeType(IncomeType.RECURRING);
		
		// Mock successful create
		Mockito.when(incomeService.createIncome(Mockito.any()))
			.thenReturn(expectedIncome);
		
		// POST to /api/budget & Assert OK response
		mockMvc.perform(post("/api/income")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Salary"))
			.andExpect(jsonPath("$.source").value("Company XYZ"))
			.andExpect(jsonPath("$.amount").value(2000.0))
			.andExpect(jsonPath("$.incomeType").value("RECURRING"));
		
		Mockito.verify(incomeService, Mockito.times(1))
			.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
		
		IncomeRequest request = new IncomeRequest();
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setIncomeType(IncomeType.RECURRING);
		
	    // POST to /api/budget & Assert bad request
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
        request.setIncomeType(IncomeType.RECURRING);
		
	    // POST to /api/budget & Assert bad request
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
        request.setIncomeType(IncomeType.RECURRING);
		
	    // POST to /api/budget & Assert bad request
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
        request.setIncomeType(IncomeType.RECURRING);
		
	    // POST to /api/budget & Assert bad request
		mockMvc.perform(post("/api/income")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.amount").value("Income amount cannot be negative."));
		
		Mockito.verify(incomeService, Mockito.times(0))
			.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenIncomeTypeIsMissing() throws Exception {
		
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
		
	    // POST to /api/budget & Assert bad request
		mockMvc.perform(post("/api/income")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.incomeType").value("Income type is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
			.createIncome(Mockito.any());
	}
	
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {
		
	    IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setIncomeType(IncomeType.RECURRING);

	    // Mock data integrity violation on create
	    Mockito.when(incomeService.createIncome(Mockito.any()))
	            .thenThrow(new DataIntegrityViolationException("Database constraint violation"));

	    // POST to /api/budget & Assert conflict status
	    mockMvc.perform(post("/api/income")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict())
	            .andExpect(jsonPath("$").value("A database constraint was violated."));
	    
	    Mockito.verify(incomeService, Mockito.times(1))
	            .createIncome(Mockito.any());
	}
}