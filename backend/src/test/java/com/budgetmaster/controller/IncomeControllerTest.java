package com.budgetmaster.controller;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.GlobalExceptionHandler;
import com.budgetmaster.exception.InvalidMonthYearExceptionHandler;
import com.budgetmaster.service.IncomeService;
import com.budgetmaster.model.Income;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebMvcTest(IncomeController.class)
@Import(GlobalExceptionHandler.class)
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
        
        YearMonth testYearMonth = YearMonth.of(2000, 1);
		Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
		
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class), Mockito.eq(testYearMonth.toString())))
				.thenReturn(expectedIncome);
		
		mockMvc.perform(post("/api/income/{monthYear}", testYearMonth)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Salary"))
				.andExpect(jsonPath("$.source").value("Company XYZ"))
				.andExpect(jsonPath("$.amount").value(2000.0))
				.andExpect(jsonPath("$.type").value("RECURRING"))
				.andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class), Mockito.eq(testYearMonth.toString()));
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
		
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class), Mockito.isNull()))
				.thenReturn(expectedIncome);
		
		mockMvc.perform(post("/api/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Salary"))
				.andExpect(jsonPath("$.source").value("Company XYZ"))
				.andExpect(jsonPath("$.amount").value(2000.0))
				.andExpect(jsonPath("$.type").value("RECURRING"))
				.andExpect(jsonPath("$.monthYear").value(defaultYearMonth.toString()));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class), Mockito.isNull());
	}
	
	@Test
	void shouldGetIncomeWhenValidMonthAndId() throws Exception {
		YearMonth testYearMonth = YearMonth.of(2000, 1);
		Income income = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
		
		Mockito.when(incomeService.getIncomeById(testYearMonth.toString(), 1L))
				.thenReturn(Optional.of(income));
		
		mockMvc.perform(get("/api/income/{monthYear}/{id}", testYearMonth, 1L)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Salary"))
				.andExpect(jsonPath("$.source").value("Company XYZ"))
				.andExpect(jsonPath("$.amount").value(2000.0))
				.andExpect(jsonPath("$.type").value("RECURRING"))
	            .andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(testYearMonth.toString(), 1L);
	}
	
	@Test
	void shouldGetAllIncomesForValidMonth() throws Exception {
		YearMonth testYearMonth = YearMonth.of(2000, 1);
		
		List<Income> incomeList = List.of(
				new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth),
				new Income("Bonus", "Company ABC", 500.0, TransactionType.ONE_TIME, testYearMonth)
		);
		
		Mockito.when(incomeService.getAllIncomesForMonth(Mockito.eq(testYearMonth.toString())))
				.thenReturn(incomeList);
		
		mockMvc.perform(get("/api/income/{monthYear}", testYearMonth)
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
				.getAllIncomesForMonth(testYearMonth.toString());
	}
	
	@Test
	void shouldReturnEmptyListWhenNoIncomesExist() throws Exception {
	    YearMonth testYearMonth = YearMonth.of(2000, 1);

	    Mockito.when(incomeService.getAllIncomesForMonth(Mockito.eq(testYearMonth.toString())))
	           .thenReturn(Collections.emptyList());

	    mockMvc.perform(get("/api/income/{monthYear}", testYearMonth.toString())
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$").isArray())
	            .andExpect(jsonPath("$.length()").value(0));

	    Mockito.verify(incomeService, Mockito.times(1))
	           .getAllIncomesForMonth(Mockito.eq(testYearMonth.toString()));
	}
	
	@Test
	void shouldUpdateIncomeWhenValidMonthAndId() throws Exception {
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
		
		Mockito.when(incomeService.updateIncome(Mockito.eq(testYearMonth.toString()), Mockito.eq(1L), Mockito.any(IncomeRequest.class)))
				.thenReturn(Optional.of(updatedIncome));
		
		mockMvc.perform(put("/api/income/{monthYear}/{id}", testYearMonth.toString(), 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Bonus"))
				.andExpect(jsonPath("$.source").value("Company ABC"))
				.andExpect(jsonPath("$.amount").value(3000.0))
				.andExpect(jsonPath("$.type").value("ONE_TIME"))
	            .andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.eq(testYearMonth.toString()), Mockito.eq(1L), Mockito.any(IncomeRequest.class));
	}
	
    @Test
    void shouldDeleteIncomeWhenValidMonthAndId() throws Exception {
    	YearMonth testYearMonth = YearMonth.of(2000, 1);
    	Mockito.doReturn(true)
    			.when(incomeService)
    			.deleteIncome(Mockito.eq(testYearMonth.toString()), Mockito.eq(1L));
    	
        mockMvc.perform(delete("/api/income/{monthYear}/{id}", testYearMonth.toString(), 1L))
                .andExpect(status().isNoContent());
        
        Mockito.verify(incomeService, Mockito.times(1))
        		.deleteIncome(Mockito.eq(testYearMonth.toString()), Mockito.eq(1L));
    }
	
	@Test
	void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
		
		mockMvc.perform(post("/api/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.name").value("Income name is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class), Mockito.isNull());
	}
	
	@Test
	void shouldReturnBadRequestWhenSourceIsMissing() throws Exception {
		IncomeRequest request = new IncomeRequest();
		request.setName("Salary");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
		
		mockMvc.perform(post("/api/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.source").value("Income source is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class), Mockito.isNull());
	}
	
	@Test
	void shouldReturnBadRequestWhenAmountIsMissing() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setType(TransactionType.RECURRING);
		
		mockMvc.perform(post("/api/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.amount").value("Income amount is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class), Mockito.isNull());
	}
	
	@Test
	void shouldReturnBadRequestWhenIncomeAmountIsNegative() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(-2000.0);
        request.setType(TransactionType.RECURRING);
		
		mockMvc.perform(post("/api/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.amount").value("Income amount cannot be negative."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class), Mockito.isNull());
	}
	
	@Test
	void shouldReturnBadRequestWhenTransactionTypeIsMissing() throws Exception {
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
		
		mockMvc.perform(post("/api/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("Income transaction type is required."));
		
		Mockito.verify(incomeService, Mockito.times(0))
				.createIncome(Mockito.any(IncomeRequest.class), Mockito.isNull());
	}
	
    @Test
    void shouldReturnBadRequestWhenMonthYearFormatIsInvalid() throws Exception {
        IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
        
        Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class), Mockito.eq("2000-jan")))
        		.thenThrow(new InvalidMonthYearExceptionHandler("Invalid YearMonth value. Expected format: YYYY-MM."));
        
        mockMvc.perform(post("/api/income/{monthYear}", "2000-jan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.monthYear").value("Invalid YearMonth value. Expected format: YYYY-MM."));

        Mockito.verify(incomeService, Mockito.times(1))
        		.createIncome(Mockito.any(IncomeRequest.class), Mockito.eq("2000-jan"));
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

	    mockMvc.perform(post("/api/income")
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

	    mockMvc.perform(post("/api/income")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(invalidRequest))
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath("$.type").value("Invalid value 'INVALID_TYPE' for 'type'. Allowed values: [RECURRING, ONE_TIME]"));
	}
	
	@Test
	void shouldReturnNotFoundWhenIncomeDoesNotExist() throws Exception {
		YearMonth monthYear = YearMonth.of(2000, 1);
		Mockito.when(incomeService.getIncomeById(Mockito.eq(monthYear.toString()), Mockito.eq(99L)))
				.thenReturn(Optional.empty());
		
		mockMvc.perform(get("/api/income/{monthYear}/{id}", monthYear.toString(), 99L)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(Mockito.eq(monthYear.toString()), Mockito.eq(99L));
	}
	
	@Test
	void shouldReturnNotFoundWhenIncomeIdDoesNotExistForValidMonthYear() throws Exception {
	    YearMonth validMonthYear = YearMonth.of(2000, 1);
	    Long nonExistentId = 99L;

	    Mockito.when(incomeService.getIncomeById(Mockito.eq(validMonthYear.toString()), Mockito.eq(nonExistentId)))
	           .thenReturn(Optional.empty());

	    mockMvc.perform(get("/api/income/{monthYear}/{id}", validMonthYear.toString(), nonExistentId)
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isNotFound());

	    Mockito.verify(incomeService, Mockito.times(1))
	    		.getIncomeById(validMonthYear.toString(), nonExistentId);
	}
	
	@Test
	void shouldReturnNotFoundWhenMonthYearDoesNotExistButIncomeIdIsValid() throws Exception {
	    String nonExistentMonthYear = "2100-02";
	    Long validIncomeId = 1L;

	    Mockito.when(incomeService.getIncomeById(Mockito.eq(nonExistentMonthYear.toString()), Mockito.eq(validIncomeId)))
	           .thenReturn(Optional.empty());

	    mockMvc.perform(get("/api/income/{monthYear}/{id}", nonExistentMonthYear, validIncomeId)
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isNotFound());

	    Mockito.verify(incomeService, Mockito.times(1))
	    		.getIncomeById(nonExistentMonthYear, validIncomeId);
	}

	
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentIncome() throws Exception {
    	YearMonth monthYear = YearMonth.of(2000, 1);
    	IncomeRequest updateRequest = new IncomeRequest();
		updateRequest.setName("Interest Income");
		updateRequest.setSource("Bank XYZ");
		updateRequest.setAmount(100.0);
		updateRequest.setType(TransactionType.ONE_TIME);
        
        Mockito.when(incomeService.updateIncome(Mockito.eq(monthYear.toString()), Mockito.eq(99L), Mockito.any(IncomeRequest.class)))
        		.thenReturn(Optional.empty());
        
        mockMvc.perform(put("/api/income/{monthYear}/{id}", monthYear.toString(), 99L)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateRequest)))
	            .andExpect(status().isNotFound());
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
		
		YearMonth monthYear = YearMonth.of(2000, 1);
	    mockMvc.perform(put("/api/income/{monthYear}/{id}", monthYear.toString(), 1L)
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
        
        YearMonth monthYear = YearMonth.of(2000, 1);
        mockMvc.perform(put("/api/income/{monthYear}/{id}", monthYear.toString(), 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
	            .andExpect(status().isBadRequest())
	            .andExpect(jsonPath("$.type").value("Invalid value 'INVALID_TYPE' for 'type'. Allowed values: [RECURRING, ONE_TIME]"));
    }
    
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentIncome() throws Exception {
    	YearMonth monthYear = YearMonth.of(2000, 1);
    	
    	Mockito.doReturn(false)
				.when(incomeService)
				.deleteIncome(Mockito.eq(monthYear.toString()), Mockito.eq(99L));
    	
        mockMvc.perform(delete("/api/income/{monthYear}/{id}", monthYear.toString(), 99L))
                .andExpect(status().isNotFound());
        
        Mockito.verify(incomeService, Mockito.times(1))
        		.deleteIncome(Mockito.eq(monthYear.toString()), Mockito.eq(99L));
    }
	
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {
		YearMonth monthYear = YearMonth.of(2000, 1);
		
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);

	    Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class), Mockito.eq(monthYear.toString())))
	            .thenThrow(new DataIntegrityViolationException("Database constraint violation"));
	    
	    mockMvc.perform(post("/api/income/{monthYear}", monthYear.toString())
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict())
	            .andExpect(jsonPath("$").value("A database constraint was violated."));
	    
	    Mockito.verify(incomeService, Mockito.times(1))
	            .createIncome(Mockito.any(IncomeRequest.class), Mockito.eq(monthYear.toString()));
	}
	
	@Test
	void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
		YearMonth monthYear = YearMonth.of(2000, 1);
		
		IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);

	    Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class), Mockito.eq(monthYear.toString())))
	            .thenThrow(new RuntimeException("Service failure"));

	    mockMvc.perform(post("/api/income/{monthYear}", monthYear.toString())
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$").value("An unexpected error occurred."));

	    Mockito.verify(incomeService, Mockito.times(1))
	    		.createIncome(Mockito.any(IncomeRequest.class), Mockito.eq(monthYear.toString()));
	}
}