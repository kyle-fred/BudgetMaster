package com.budgetmaster.controller;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.IncomeNotFoundException;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

@WebMvcTest(IncomeController.class)
public class IncomeControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
    private IncomeService incomeService;
	
	@Test
	void shouldCreateIncomeWhenValidRequest() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
        request.setMonthYear("2000-01");
        
        YearMonth testYearMonth = YearMonth.of(2000, 1);
		Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
		
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenReturn(expectedIncome);
		
		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Salary"))
				.andExpect(jsonPath("$.source").value("Company XYZ"))
				.andExpect(jsonPath("$.amount").value(2000.0))
				.andExpect(jsonPath("$.type").value("RECURRING"))
				.andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void shouldCreateIncomeWhenMonthYearIsNotProvided() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
		
		YearMonth defaultYearMonth = YearMonth.now();
		Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, defaultYearMonth);
		
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenReturn(expectedIncome);
		
		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Salary"))
				.andExpect(jsonPath("$.source").value("Company XYZ"))
				.andExpect(jsonPath("$.amount").value(2000.0))
				.andExpect(jsonPath("$.type").value("RECURRING"))
				.andExpect(jsonPath("$.monthYear").value(defaultYearMonth.toString()));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void shouldGetIncomeWhenValidId() throws Exception {
        YearMonth testYearMonth = YearMonth.of(2000, 1);
		Income income = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
		
		Mockito.when(incomeService.getIncomeById(1L))
				.thenReturn(income);
		
		mockMvc.perform(get("/api/incomes/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Salary"))
				.andExpect(jsonPath("$.source").value("Company XYZ"))
				.andExpect(jsonPath("$.amount").value(2000.0))
				.andExpect(jsonPath("$.type").value("RECURRING"))
	            .andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(1L);
	}
	
	@Test
	void shouldGetAllIncomesForValidMonth() throws Exception {
		YearMonth testYearMonth = YearMonth.of(2000, 1);
		
		List<Income> incomeList = List.of(
				new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth),
				new Income("Bonus", "Company ABC", 500.0, TransactionType.ONE_TIME, testYearMonth)
		);
		
		Mockito.when(incomeService.getAllIncomesForMonth("2000-01"))
				.thenReturn(incomeList);
		
		mockMvc.perform(get("/api/incomes")
				.param("monthYear", "2000-01")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
	            .andExpect(jsonPath("$[0].name").value("Salary"))
	            .andExpect(jsonPath("$[0].source").value("Company XYZ"))
	            .andExpect(jsonPath("$[0].amount").value(2000.0))
	            .andExpect(jsonPath("$[0].type").value("RECURRING"))
	            .andExpect(jsonPath("$[0].monthYear").value("2000-01"))
	            .andExpect(jsonPath("$[1].name").value("Bonus"))
	            .andExpect(jsonPath("$[1].source").value("Company ABC"))
	            .andExpect(jsonPath("$[1].amount").value(500.0))
	            .andExpect(jsonPath("$[1].type").value("ONE_TIME"))
	            .andExpect(jsonPath("$[1].monthYear").value("2000-01"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getAllIncomesForMonth("2000-01");
	}
	
	@Test
	void shouldReturnEmptyListWhenNoIncomesExist() throws Exception {
	    Mockito.when(incomeService.getAllIncomesForMonth("2000-01"))
	           .thenReturn(Collections.emptyList());

	    mockMvc.perform(get("/api/incomes")
	            .param("monthYear", "2000-01")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$").isArray())
	            .andExpect(jsonPath("$.length()").value(0));

	    Mockito.verify(incomeService, Mockito.times(1))
	           .getAllIncomesForMonth("2000-01");
	}
	
	@Test
	void shouldUpdateIncomeWhenValidId() throws Exception {
		YearMonth testYearMonth = YearMonth.of(2000, 1);
		Income existingIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
		existingIncome.setId(1L);
		
		Income updatedIncome = new Income ("Bonus", "Company ABC", 3000.0, TransactionType.ONE_TIME, testYearMonth);
		updatedIncome.setId(1L);
		
		IncomeRequest updateRequest = new IncomeRequest();
		updateRequest.setName("Bonus");
		updateRequest.setSource("Company ABC");
		updateRequest.setAmount(3000.0);
		updateRequest.setType(TransactionType.ONE_TIME);
		
		Mockito.when(incomeService.updateIncome(Mockito.eq(1L), Mockito.refEq(updateRequest)))
				.thenReturn(updatedIncome);
		
		mockMvc.perform(put("/api/incomes/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Bonus"))
				.andExpect(jsonPath("$.source").value("Company ABC"))
				.andExpect(jsonPath("$.amount").value(3000.0))
				.andExpect(jsonPath("$.type").value("ONE_TIME"))
	            .andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.eq(1L), Mockito.refEq(updateRequest));
	}
	
    @Test
    void shouldDeleteIncomeWhenValidId() throws Exception {     
    	Long incomeId = 1L;
    	
    	Mockito.doNothing()
                .when(incomeService)
                .deleteIncome(incomeId);
    	
        mockMvc.perform(delete("/api/incomes/{id}", incomeId))
                .andExpect(status().isNoContent());
        
        Mockito.verify(incomeService, Mockito.times(1))
                .deleteIncome(incomeId);
    }
	
	@Test
	void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
		
		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.name").value("Income name is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void shouldReturnBadRequestWhenSourceIsMissing() throws Exception {
		IncomeRequest request = new IncomeRequest();
		request.setName("Salary");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
		
		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.source").value("Income source is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void shouldReturnBadRequestWhenAmountIsMissing() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setType(TransactionType.RECURRING);
		
		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.amount").value("Income amount is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void shouldReturnBadRequestWhenIncomeAmountIsNegative() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(-2000.0);
        request.setType(TransactionType.RECURRING);
		
		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.amount").value("Income amount cannot be negative."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void shouldReturnBadRequestWhenTransactionTypeIsMissing() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
		
		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("Income transaction type is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
    @Test
    void shouldReturnBadRequestWhenMonthYearFormatIsInvalid() throws Exception {
        IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
        request.setMonthYear("2000-jan");
        
        mockMvc.perform(post("/api/incomes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.monthYear").value("Month year must be in format YYYY-MM"));

        Mockito.verify(incomeService, Mockito.times(0))
        		.createIncome(Mockito.any(IncomeRequest.class));
    }
	
	@Test
	void shouldReturnBadRequestForMalformedJsonIncome() throws Exception {
	    String malformedJson = """
	        {
	            "name": "Bonus",
	            "amount": 200.0,
	            "type": 
	        }
	        """;

	    mockMvc.perform(post("/api/incomes")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(malformedJson))
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.error").value("Invalid request format."));
	}
	
	@Test
	void shouldReturnBadRequestWhenIncomeTypeIsInvalid() throws Exception {
	    String invalidRequest = """
	        {
	            "name": "Salary",
	            "amount": 3000.0,
	            "type": "INVALID_TYPE"
	        }
	        """;

	    mockMvc.perform(post("/api/incomes")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(invalidRequest))
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.type").value("Invalid value 'INVALID_TYPE' for 'type'. Allowed values: [RECURRING, ONE_TIME]"));
	}
	
	@Test
	void shouldReturnNotFoundWhenIncomeDoesNotExist() throws Exception {
		Mockito.when(incomeService.getIncomeById(99L))
				.thenThrow(new IncomeNotFoundException("Income not found with id: 99"));

		mockMvc.perform(get("/api/incomes/{id}", 99L)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Income not found with id: 99"));

		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(99L);
	}
	
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentIncome() throws Exception {
	    Long incomeId = 1L;
	    IncomeRequest request = new IncomeRequest();
	    request.setName("Salary");
	    request.setSource("Company XYZ");
	    request.setAmount(2000.0);
	    request.setType(TransactionType.RECURRING);
	    request.setMonthYear("2000-01");
	
	    
	    
	    Mockito.when(incomeService.updateIncome(Mockito.eq(incomeId), Mockito.any(IncomeRequest.class)))
	            .thenThrow(new IncomeNotFoundException("Income not found with id: " + incomeId));
	    
	    mockMvc.perform(put("/api/incomes/{id}", incomeId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isNotFound())
	            .andExpect(jsonPath("$.error").value("Income not found with id: " + incomeId));
	    
	    Mockito.verify(incomeService, Mockito.times(1))
	            .updateIncome(Mockito.eq(incomeId), Mockito.any(IncomeRequest.class));
    }
    
	@Test
	void shouldReturnBadRequestWhenUpdatingWithMalformedRequest() throws Exception {
		String malformedJson = """
	        {
	            "name": "Rent",
	            "amount": 1000.0,
	            "category": 
	        }
	        """;
		
	    mockMvc.perform(put("/api/incomes/{id}", 1L)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(malformedJson))
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.error").value("Invalid request format."));
	}
    
    @Test
    void shouldReturnBadRequestWhenUpdatingIncomeWithInvalidType() throws Exception {
        String invalidRequest = """
            {
                "name": "Freelance Work",
                "amount": 500.0,
                "type": "INVALID_TYPE"
            }
            """;
        
        mockMvc.perform(put("/api/incomes/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
	            .andExpect(status().isBadRequest())
	            .andExpect(jsonPath("$.type").value("Invalid value 'INVALID_TYPE' for 'type'. Allowed values: [RECURRING, ONE_TIME]"));
    }
    
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentIncome() throws Exception {
    	Long incomeId = 1L;
    	
        Mockito.doThrow(new IncomeNotFoundException("Income not found with id: " + incomeId))
        		.when(incomeService).deleteIncome(incomeId);
    	
        mockMvc.perform(delete("/api/incomes/{id}", incomeId))
                .andExpect(status().isNotFound());
        
        Mockito.verify(incomeService, Mockito.times(1))
                .deleteIncome(incomeId);
    }
	
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {	
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);

	    Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
	            .thenThrow(new DataIntegrityViolationException("Database constraint violation"));
	    
	    mockMvc.perform(post("/api/incomes")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict())
	            .andExpect(jsonPath("$").value("A database constraint was violated."));
	    
	    Mockito.verify(incomeService, Mockito.times(1))
	            .createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);

	    Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
	            .thenThrow(new RuntimeException("Service failure"));

	    mockMvc.perform(post("/api/incomes")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$").value("An unexpected error occurred."));

	    Mockito.verify(incomeService, Mockito.times(1))
	    		.createIncome(Mockito.any(IncomeRequest.class));
	}
}