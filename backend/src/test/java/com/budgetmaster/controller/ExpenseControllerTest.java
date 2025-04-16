package com.budgetmaster.controller;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.ExpenseNotFoundException;
import com.budgetmaster.service.ExpenseService;
import com.budgetmaster.model.Expense;
import com.budgetmaster.model.value.Money;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;

@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {
	// -- Dependencies --
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
    private ExpenseService expenseService;

	// -- Test Data --
	private static final Long testId = 1L;
	private static final String testName = "Test Expense";
	private static final BigDecimal testAmount = new BigDecimal("123.45");
	private static final Currency testCurrency = Currency.getInstance("GBP");
	private static final TransactionType testType = TransactionType.ONE_TIME;
	private static final ExpenseCategory testCategory = ExpenseCategory.MISCELLANEOUS;
	private static final String testMonth = "2000-01";
	private static final YearMonth testYearMonth = YearMonth.of(2000, 1);

	// -- Test Objects --
	private ExpenseRequest expenseRequest;
	private Expense expense;
	private MoneyRequest moneyRequest;

	// -- Setup --

	@BeforeEach
	void setUp() {
		// Setup MoneyRequest
		moneyRequest = new MoneyRequest();
		moneyRequest.setAmount(testAmount);
		moneyRequest.setCurrency(testCurrency);

		// Setup ExpenseRequest
		expenseRequest = new ExpenseRequest();
		expenseRequest.setName(testName);
		expenseRequest.setMoney(moneyRequest);
		expenseRequest.setCategory(testCategory);
		expenseRequest.setType(testType);
		expenseRequest.setMonth(testMonth);
	
		expense = new Expense();
		expense.setId(testId);
		expense.setName(testName);
		expense.setMoney(Money.of(testAmount, testCurrency));
		expense.setCategory(testCategory);
		expense.setType(testType);
		expense.setMonth(testYearMonth);
	}
	
	// -- Create Expense Tests --
	
	@Test
	void createExpense_ValidRequest_ReturnsCreated() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenReturn(expense);	
				
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(testName))
				.andExpect(jsonPath("$.money.amount").value(testAmount.toString()))
				.andExpect(jsonPath("$.money.currency").value(testCurrency.toString()))
				.andExpect(jsonPath("$.category").value(testCategory.toString()))
				.andExpect(jsonPath("$.type").value(testType.toString()))
				.andExpect(jsonPath("$.month").value(testMonth));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	// -- Get Expense Tests --
	
	@Test
	void getExpense_ValidId_ReturnsOk() throws Exception {
		Mockito.when(expenseService.getExpenseById(testId))
				.thenReturn(expense);
				
		mockMvc.perform(get("/api/expenses/{id}", testId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(testName))
				.andExpect(jsonPath("$.money.amount").value(testAmount.toString()))
				.andExpect(jsonPath("$.money.currency").value(testCurrency.toString()))
				.andExpect(jsonPath("$.category").value(testCategory.toString()))
				.andExpect(jsonPath("$.type").value(testType.toString()))
				.andExpect(jsonPath("$.month").value(testMonth));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(testId);
	}

	@Test
	void getExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.getExpenseById(99L))
				.thenThrow(new ExpenseNotFoundException("Expense not found with id: 99"));
				
		mockMvc.perform(get("/api/expenses/{id}", 99L)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Expense not found with id: 99"));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getExpenseById(99L);
	}

	// -- Get All Expenses Tests --
	
	@Test
	void getAllExpenses_ValidMonth_ReturnsOk() throws Exception {
		List<Expense> expenseList = List.of(expense, expense);
		
		Mockito.when(expenseService.getAllExpensesForMonth(testMonth))
				.thenReturn(expenseList);
				
		mockMvc.perform(get("/api/expenses")
				.param("month", testMonth)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value(testName))
				.andExpect(jsonPath("$[1].name").value(testName));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.getAllExpensesForMonth(testMonth);
	}
	
	// -- Update Expense Tests --
	
	@Test
	void updateExpense_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenReturn(expense);
				
		mockMvc.perform(put("/api/expenses/{id}", testId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(testName))
				.andExpect(jsonPath("$.money.amount").value(testAmount.toString()))
				.andExpect(jsonPath("$.money.currency").value(testCurrency.toString()))
				.andExpect(jsonPath("$.category").value(testCategory.toString()))
				.andExpect(jsonPath("$.type").value(testType.toString()))
				.andExpect(jsonPath("$.month").value(testMonth));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}

	@Test
	void updateExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(expenseService.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class)))
				.thenThrow(new ExpenseNotFoundException("Expense not found with id: 99"));
				
		mockMvc.perform(put("/api/expenses/{id}", 99L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Expense not found with id: 99"));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.updateExpense(Mockito.any(Long.class), Mockito.any(ExpenseRequest.class));
	}
	
	// -- Delete Expense Tests --
	
	@Test
	void deleteExpense_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(expenseService)
				.deleteExpense(testId);
				
		mockMvc.perform(delete("/api/expenses/{id}", testId))
				.andExpect(status().isNoContent());
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(testId);
	}
	
	@Test
	void deleteExpense_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new ExpenseNotFoundException("Expense not found with id: 99"))
				.when(expenseService)
				.deleteExpense(99L);
				
		mockMvc.perform(delete("/api/expenses/{id}", 99L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Expense not found with id: 99"));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.deleteExpense(99L);
	}
	
	// -- Error Handling Tests --
	
	@Test
	void createExpense_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new RuntimeException("Service failure"));
				
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$").value("An unexpected error occurred."));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
	
	@Test
	void createExpense_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(expenseService.createExpense(Mockito.any(ExpenseRequest.class)))
				.thenThrow(new DataIntegrityViolationException("Database constraint violation"));
				
		mockMvc.perform(post("/api/expenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expenseRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$").value("A database constraint was violated."));
				
		Mockito.verify(expenseService, Mockito.times(1))
				.createExpense(Mockito.any(ExpenseRequest.class));
	}
}