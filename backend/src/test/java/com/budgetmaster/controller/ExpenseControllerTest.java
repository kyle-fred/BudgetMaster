package com.budgetmaster.controller;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.ExpenseNotFoundException;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;
import java.util.List;

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
        
        YearMonth testMonth = YearMonth.of(2000, 1);
		
		Expense expectedExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testMonth);
		
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenReturn(expectedExpense);
		
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Rent"))
				.andExpect(jsonPath("$.amount").value(1000.0))
				.andExpect(jsonPath("$.category").value("HOUSING"))
				.andExpect(jsonPath("$.type").value("RECURRING"))
				.andExpect(jsonPath("$.month").value("2000-01"));
		
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
 	void shouldCreateExpenseWhenMonthIsNotProvided() throws Exception {
		ExpenseRequest request = new ExpenseRequest();
		request.setName("Rent");
		request.setAmount(1000.0);
		request.setCategory(ExpenseCategory.HOUSING);
		request.setType(TransactionType.RECURRING);
		
		YearMonth defaultMonth = YearMonth.now();
		
		Expense expectedExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, defaultMonth);
		
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenReturn(expectedExpense);
		
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Rent"))
				.andExpect(jsonPath("$.amount").value(1000.0))
				.andExpect(jsonPath("$.type").value("RECURRING"))
				.andExpect(jsonPath("$.category").value("HOUSING"))
				.andExpect(jsonPath("$.month").value(defaultMonth.toString()));
		 
		 Mockito.verify(expenseService, Mockito.times(1))
		 		.createExpense(Mockito.any(ExpenseRequest.class));
 	}
	
	@Test
	void shouldGetExpenseWhenValidId() throws Exception {
		YearMonth testMonth = YearMonth.of(2000, 1);
		Expense expense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testMonth);
		
		Mockito.when(expenseService.getExpenseById(Mockito.eq(1L)))
				.thenReturn(expense);
		
		mockMvc.perform(get("/api/expenses/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Rent"))
				.andExpect(jsonPath("$.amount").value(1000.0))
				.andExpect(jsonPath("$.category").value("HOUSING"))
				.andExpect(jsonPath("$.type").value("RECURRING"))
				.andExpect(jsonPath("$.month").value("2000-01"));
		
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(Mockito.eq(1L));
	}
	
	@Test
 	void shouldGetAllExpensesForValidMonth() throws Exception {
 		YearMonth testMonth = YearMonth.of(2000, 1);
 		
 		List<Expense> expenseList = List.of(
 				new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testMonth),
 				new Expense("Gas", 100.0, ExpenseCategory.UTILITIES, TransactionType.RECURRING, testMonth)
 		);
 		
 		Mockito.when(expenseService.getAllExpensesForMonth(Mockito.eq(testMonth.toString())))
 				.thenReturn(expenseList);
 		
 		mockMvc.perform(get("/api/expenses")
 				.param("month", testMonth.toString())
 				.contentType(MediaType.APPLICATION_JSON))
 				.andExpect(status().isOk())
 	            .andExpect(jsonPath("$[0].name").value("Rent"))
 	            .andExpect(jsonPath("$[0].amount").value(1000.0))
 	            .andExpect(jsonPath("$[0].category").value("HOUSING"))
 	            .andExpect(jsonPath("$[0].type").value("RECURRING"))
 	            .andExpect(jsonPath("$[0].month").value("2000-01"))
 	            .andExpect(jsonPath("$[1].name").value("Gas"))
 	            .andExpect(jsonPath("$[1].amount").value(100.0))
 	            .andExpect(jsonPath("$[1].category").value("UTILITIES"))
 	            .andExpect(jsonPath("$[1].type").value("RECURRING"))
 	            .andExpect(jsonPath("$[1].month").value("2000-01"));
 		
 		Mockito.verify(expenseService, Mockito.times(1))
 				.getAllExpensesForMonth(testMonth.toString());
 	}
 	
 	@Test
 	void shouldReturnNotFoundWhenNoExpensesExist() throws Exception {
 	    YearMonth testMonth = YearMonth.of(2000, 1);
 
 	    Mockito.when(expenseService.getAllExpensesForMonth(Mockito.eq(testMonth.toString())))
 	           .thenThrow(new ExpenseNotFoundException("No expenses found for month: " + testMonth));
		
 	    mockMvc.perform(get("/api/expenses")
 	            .param("month", testMonth.toString())
 	            .contentType(MediaType.APPLICATION_JSON))
 	            .andExpect(status().isNotFound())
 	            .andExpect(jsonPath("$.error").value("No expenses found for month: " + testMonth));
		
 	    Mockito.verify(expenseService, Mockito.times(1))
 	           .getAllExpensesForMonth(Mockito.eq(testMonth.toString()));
 	}
	
	@Test
	void shouldUpdateExpenseWhenValidId() throws Exception {
		YearMonth testMonth = YearMonth.of(2000, 1);
		Expense updatedExpense = new Expense("Gas Bill", 100.0, ExpenseCategory.UTILITIES, TransactionType.RECURRING, testMonth);
		
		ExpenseRequest updateRequest = new ExpenseRequest();
		updateRequest.setName("Gas Bill");
		updateRequest.setAmount(100.0);
		updateRequest.setCategory(ExpenseCategory.UTILITIES);
		updateRequest.setType(TransactionType.RECURRING);
		
		Mockito.when(expenseService.updateExpense(Mockito.eq(1L), Mockito.any(ExpenseRequest.class)))
				.thenReturn(updatedExpense);
		
		mockMvc.perform(put("/api/expenses/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Gas Bill"))
				.andExpect(jsonPath("$.amount").value(100.0))
				.andExpect(jsonPath("$.category").value("UTILITIES"))
				.andExpect(jsonPath("$.type").value("RECURRING"))
				.andExpect(jsonPath("$.month").value("2000-01"));
		
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.eq(1L), Mockito.any(ExpenseRequest.class));
	}
	
	@Test
    void shouldDeleteExpenseWhenValidId() throws Exception {
    	Long expenseId = 1L;

    	Mockito.doNothing()
                .when(expenseService)
                .deleteExpense(expenseId);

        mockMvc.perform(delete("/api/expenses/{id}", expenseId))
                .andExpect(status().isNoContent());

        Mockito.verify(expenseService, Mockito.times(1))
        		.deleteExpense(expenseId);
    }
	
	@Test
	void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
		ExpenseRequest request = new ExpenseRequest();
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
        
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.name").value("Expense name is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void shouldReturnBadRequestWhenAmountIsMissing() throws Exception {
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
        
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.amount").value("Expense amount is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void shouldReturnBadRequestWhenExpenseAmountIsNegative() throws Exception {
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(-1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
        
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.amount").value("Expense amount cannot be negative."));
		
		Mockito.verify(expenseService, Mockito.times(0))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void shouldReturnBadRequestWhenExpenseCategoryIsMissing() throws Exception {
		ExpenseRequest request = new ExpenseRequest();
		request.setName("Rent");
        request.setAmount(1000.0);
        request.setType(TransactionType.RECURRING);
        
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.category").value("Expense category is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void shouldReturnBadRequestWhenTransactionTypeIsMissing() throws Exception {
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("Expense transaction type is required."));
		
		Mockito.verify(expenseService, Mockito.times(0))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
    @Test
    void shouldReturnBadRequestWhenMonthFormatIsInvalid() throws Exception {
    	ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setType(TransactionType.RECURRING);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setMonth("2000-jan");
        
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.month").value("Month must be in format YYYY-MM"));

        Mockito.verify(expenseService, Mockito.times(0))
 				.createExpense(Mockito.any(ExpenseRequest.class));
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

	    mockMvc.perform(post("/api/expenses")
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

	    mockMvc.perform(post("/api/expenses")
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

	    mockMvc.perform(post("/api/expenses")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(invalidRequest))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.type").value("Invalid value 'INVALID_TYPE' for 'type'. Allowed values: [RECURRING, ONE_TIME]"));
	}
	
	@Test
	void shouldReturnNotFoundWhenExpenseDoesNotExist() throws Exception {
		Mockito.when(expenseService.getExpenseById(Mockito.eq(99L)))
				.thenThrow(new ExpenseNotFoundException("Expense not found with id: 99"));

		mockMvc.perform(get("/api/expenses/{id}", 99L)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Expense not found with id: 99"));

		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(Mockito.eq(99L));
	}
	
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentExpense() throws Exception {
	    Long expenseId = 1L;
	    ExpenseRequest request = new ExpenseRequest();
	    request.setName("Rent");
	    request.setAmount(2000.0);
	    request.setCategory(ExpenseCategory.HOUSING);
	    request.setType(TransactionType.RECURRING);
	    request.setMonth("2000-01");

	    Mockito.when(expenseService.updateExpense(Mockito.eq(expenseId), Mockito.any(ExpenseRequest.class)))
	            .thenThrow(new ExpenseNotFoundException("Expense not found with id: " + expenseId));

	    mockMvc.perform(put("/api/expenses/{id}", expenseId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isNotFound())
	            .andExpect(jsonPath("$.error").value("Expense not found with id: " + expenseId));

		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.eq(expenseId), Mockito.any(ExpenseRequest.class));
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
	    
	    mockMvc.perform(put("/api/expenses/{id}", 1L)
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
        
        mockMvc.perform(put("/api/expenses/{id}", 1L)
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
			
        mockMvc.perform(put("/api/expenses/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
	            .andExpect(status().isBadRequest())
	            .andExpect(jsonPath("$.type").value("Invalid value 'INVALID_TYPE' for 'type'. Allowed values: [RECURRING, ONE_TIME]"));
    }
    
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentExpense() throws Exception {
    	Long expenseId = 1L;

        Mockito.doThrow(new ExpenseNotFoundException("Expense not found with id: " + expenseId))
        		.when(expenseService).deleteExpense(expenseId);
    	
		mockMvc.perform(delete("/api/expenses/{id}", expenseId))
                .andExpect(status().isNotFound());
        
        Mockito.verify(expenseService, Mockito.times(1))
        		.deleteExpense(expenseId);
    }
	
	@Test
	void shouldReturnConflictWhenDataIntegrityViolationOccurs() throws Exception {
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
        
	    Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
	            .thenThrow(new DataIntegrityViolationException("Database constraint violation"));
	    
	    mockMvc.perform(post("/api/expenses")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict())
	            .andExpect(jsonPath("$").value("A database constraint was violated."));
	    
	    Mockito.verify(expenseService, Mockito.times(1))
	    		.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
		ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
        
        Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
	            .thenThrow(new RuntimeException("Service failure"));
	    
        mockMvc.perform(post("/api/expenses")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$").value("An unexpected error occurred."));
        
	    Mockito.verify(expenseService, Mockito.times(1))
	    		.createExpense(Mockito.any(ExpenseRequest.class));
	}
}