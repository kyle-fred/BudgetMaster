package com.budgetmaster.controller;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
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

import java.time.LocalDateTime;
import java.time.YearMonth;
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
	void shouldCreateExpenseWhenValidRequest() throws Exception {

		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
        request.setMonthYear("2000-01");
        
        YearMonth testYearMonth = YearMonth.of(2000, 1);
		
		Expense expectedExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testYearMonth);
		
		// Mock successful create
		Mockito.when(expenseService.createExpense(Mockito.any()))
			.thenReturn(expectedExpense);
		
		// POST to /api/expense & Assert OK response
		mockMvc.perform(post("/api/expense")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Rent"))
			.andExpect(jsonPath("$.amount").value(1000.0))
			.andExpect(jsonPath("$.category").value("HOUSING"))
			.andExpect(jsonPath("$.type").value("RECURRING"))
			.andExpect(jsonPath("$.type").value("RECURRING"))
 			.andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		Mockito.verify(expenseService, Mockito.times(1))
			.createExpense(Mockito.any());
	}
	
	@Test
 	void shouldCreateIncomeWhenMonthYearIsNotProvided() throws Exception {
		ExpenseRequest request = new ExpenseRequest();
		request.setName("Rent");
		request.setAmount(1000.0);
		request.setCategory(ExpenseCategory.HOUSING);
		request.setType(TransactionType.RECURRING);
		
		request.setMonthYear(null);
		
		YearMonth defaultYearMonth = YearMonth.now();
		
		Expense expectedExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, defaultYearMonth);
		
		Mockito.when(expenseService.createExpense(Mockito.any()))
			.thenReturn(expectedExpense);
		
		mockMvc.perform(post("/api/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Rent"))
				.andExpect(jsonPath("$.amount").value(1000.0))
				.andExpect(jsonPath("$.type").value("RECURRING"))
				.andExpect(jsonPath("$.category").value("HOUSING"))
				.andExpect(jsonPath("$.monthYear").value(defaultYearMonth.toString()));
		 
		 Mockito.verify(expenseService, Mockito.times(1))
		 	.createExpense(Mockito.any());
 	}
	
	@Test
	void shouldGetExpenseWhenValidId() throws Exception {
		
		YearMonth testYearMonth = YearMonth.of(2000, 1);
 		
		Expense expense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testYearMonth);
		
		// Mock successful read
		Mockito.when(expenseService.getExpenseById(1L)).thenReturn(Optional.of(expense));
		
		// GET /api/expense & Assert OK response
		mockMvc.perform(get("/api/expense/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Rent"))
				.andExpect(jsonPath("$.amount").value(1000.0))
				.andExpect(jsonPath("$.category").value("HOUSING"))
				.andExpect(jsonPath("$.type").value("RECURRING"))
 	            .andExpect(jsonPath("$.monthYear").value("2000-01"));
		
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(1L);
	}
	
	@Test
	void shouldUpdateExpenseWhenValidId() throws Exception {
		YearMonth updatedYearMonth = YearMonth.of(2020, 1);
		Expense updatedExpense = new Expense("Gas Bill", 100.0, ExpenseCategory.UTILITIES, TransactionType.RECURRING, updatedYearMonth);
		
		ExpenseRequest updateRequest = new ExpenseRequest();
		updateRequest.setName("Gas Bill");
		updateRequest.setAmount(100.0);
		updateRequest.setCategory(ExpenseCategory.UTILITIES);
		updateRequest.setType(TransactionType.RECURRING);
		updateRequest.setMonthYear("2020-01");
		
		// Mock successful update
		Mockito.when(expenseService.updateExpense(Mockito.eq(1L), Mockito.any(ExpenseRequest.class)))
				.thenReturn(Optional.of(updatedExpense));
		
		// PUT /api/expense & Assert OK response
		mockMvc.perform(put("/api/expense/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Gas Bill"))
				.andExpect(jsonPath("$.amount").value(100.0))
				.andExpect(jsonPath("$.category").value("UTILITIES"))
				.andExpect(jsonPath("$.type").value("RECURRING"))
	            .andExpect(jsonPath("$.monthYear").value("2020-01"));
	}
	
	@Test
    void shouldDeleteExpenseWhenValidId() throws Exception {
    	// Mock successful deletion
    	Mockito.when(expenseService.deleteExpense(1L)).thenReturn(true);
    	
    	// DELETE /api/expense & Assert OK response
        mockMvc.perform(delete("/api/expense/1"))
                .andExpect(status().isNoContent());
        
        Mockito.verify(expenseService, Mockito.times(1)).deleteExpense(1L);
    }
	
	@Test
	void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
		
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
	void shouldReturnBadRequestWhenAmountIsMissing() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
		
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
        request.setAmount(-1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
		
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
	void shouldReturnBadRequestWhenExpenseCategoryIsMissing() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
		request.setName("Rent");
        request.setAmount(1000.0);
        request.setType(TransactionType.RECURRING);
		
	    // POST to /api/expense & Assert bad request
		mockMvc.perform(post("/api/expense")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.category").value("Expense category is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
			.createExpense(Mockito.any());
	}
	
	@Test
	void shouldReturnBadRequestWhenTransactionTypeIsMissing() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
		
	    // POST to /api/expense & Assert bad request
		mockMvc.perform(post("/api/expense")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("Expense transaction type is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
			.createExpense(Mockito.any());
	}
	
    @Test
    void shouldReturnBadRequestWhenMonthYearFormatIsInvalid() throws Exception {
        
    	ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setType(TransactionType.RECURRING);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setMonthYear("2000/01");

        mockMvc.perform(post("/api/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.monthYear").value("Invalid monthYear value. Expected format: YYYY-MM."));

        Mockito.verify(expenseService, Mockito.times(0)).createExpense(Mockito.any());
    }
	
	@Test
	void shouldReturnBadRequestForMalformedJsonExpense() throws Exception {
	    String malformedJson = """
	        {
	            "name": "Rent",
	            "amount": 1000.0,
	            "category": 
	        }
	        """;

	    mockMvc.perform(post("/api/expense")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(malformedJson))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.error").value("Invalid request format."));
	}

	
	@Test
	void shouldReturnBadRequestWhenExpenseCategoryIsInvalid() throws Exception {
	    String invalidRequest = """
	        {
	            "name": "Rent",
	            "amount": 1000.0,
	            "category": "INVALID_CATEGORY",
	            "type": "RECURRING"
	        }
	        """;

	    mockMvc.perform(post("/api/expense")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(invalidRequest))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.category").value("Invalid value 'INVALID_CATEGORY' for 'category'. Allowed values: [HOUSING, UTILITIES, TAXES, MOBILE_PHONE, GROCERIES, DINING_OUT, TRANSPORT, HEALTH, FITNESS, DEBT_REPAYMENT, SUBSCIPTIONS, HOBBIES, EVENTS, CLOTHING_AND_ACCESSORIES, ELECTRONICS, HOME_AND_DECOR, EDUCATION, GIFTS_AND_DONATIONS, PETS, MISCELLANEOUS]"));
	}
	
	@Test
	void shouldReturnBadRequestWhenExpenseTypeIsInvalid() throws Exception {
	    String invalidRequest = """
	        {
	            "name": "Subscription",
	            "amount": 15.0,
	            "category": "TRANSPORT",
	            "type": "INVALID_TYPE"
	        }
	        """;

	    mockMvc.perform(post("/api/expense")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(invalidRequest))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.type").value("Invalid value 'INVALID_TYPE' for 'type'. Allowed values: [RECURRING, ONE_TIME]"));
	}
	
	@Test
	void shouldReturnNotFoundWhenExpenseDoesNotExist() throws Exception {
		// Mock failed read
		Mockito.when(expenseService.getExpenseById(99L)).thenReturn(Optional.empty());
		
		// GET /api/expense & Assert not found response
		mockMvc.perform(get("/api/expense/99")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(99L);
	}
	
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentExpense() throws Exception {
    	
        ExpenseRequest updateRequest = new ExpenseRequest();
		updateRequest.setName("Rent");
		updateRequest.setAmount(2000.0);
		updateRequest.setCategory(ExpenseCategory.HOUSING);
		updateRequest.setType(TransactionType.RECURRING);
        
        // Mock failed update
        Mockito.when(expenseService.updateExpense(99L, updateRequest)).thenReturn(Optional.empty());
        
        // PUT /api/expense & Assert not found response
        mockMvc.perform(put("/api/expense/99")
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

	    mockMvc.perform(put("/api/expense/{id}", 1)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(malformedJson))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.error").value("Invalid request format."));
	}
    
    @Test
    void shouldReturnBadRequestWhenUpdatingExpenseWithInvalidCategory() throws Exception {
        String invalidRequest = """
            {
                "name": "Groceries",
                "amount": 200.0,
                "category": "INVALID_CATEGORY",
                "type": "ONE_TIME"
            }
            """;

        mockMvc.perform(put("/api/expense/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.category").value("Invalid value 'INVALID_CATEGORY' for 'category'. Allowed values: [HOUSING, UTILITIES, TAXES, MOBILE_PHONE, GROCERIES, DINING_OUT, TRANSPORT, HEALTH, FITNESS, DEBT_REPAYMENT, SUBSCIPTIONS, HOBBIES, EVENTS, CLOTHING_AND_ACCESSORIES, ELECTRONICS, HOME_AND_DECOR, EDUCATION, GIFTS_AND_DONATIONS, PETS, MISCELLANEOUS]"));
    }
    
    @Test
    void shouldReturnBadRequestWhenUpdatingExpenseWithInvalidType() throws Exception {
        String invalidRequest = """
            {
                "name": "Car Insurance",
                "amount": 500.0,
                "category": "TRANSPORT",
                "type": "INVALID_TYPE"
            }
            """;

        mockMvc.perform(put("/api/expense/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("Invalid value 'INVALID_TYPE' for 'type'. Allowed values: [RECURRING, ONE_TIME]"));
    }
    
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentExpense() throws Exception {
    	// Mock failed deletion (ID does not exist)
    	Mockito.when(expenseService.deleteExpense(1L)).thenReturn(false);
    	
    	// DELETE /api/expense & Assert not found response
        mockMvc.perform(delete("/api/expense/1"))
                .andExpect(status().isNotFound());
        
        Mockito.verify(expenseService, Mockito.times(1)).deleteExpense(1L);
    }
	
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {
		
	    ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);

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
	
	@Test
	void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
		
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);

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
}