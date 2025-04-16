package com.budgetmaster.controller;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.IncomeNotFoundException;
import com.budgetmaster.service.IncomeService;
import com.budgetmaster.model.Income;
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
import java.util.List;
import java.util.Currency;

@WebMvcTest(IncomeController.class)
public class IncomeControllerTest {
	// -- Dependencies --
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private IncomeService incomeService;

	// -- Test Data --
	private static final Long testId = 1L;
	private static final String testName = "Test Income";
	private static final String testSource = "Test Source";
	private static final BigDecimal testAmount = new BigDecimal("123.45");
	private static final Currency testCurrency = Currency.getInstance("GBP");
	private static final TransactionType testType = TransactionType.ONE_TIME;
	private static final String testMonth = "2000-01";
	private static final YearMonth testYearMonth = YearMonth.of(2000, 1);
	
	// -- Test Objects --
	private IncomeRequest incomeRequest;
	private Income testIncome;
	private MoneyRequest moneyRequest;
	
	// -- Setup --
	@BeforeEach
	void setUp() {
		// Setup MoneyRequest
		moneyRequest = new MoneyRequest();
		moneyRequest.setAmount(testAmount);
		moneyRequest.setCurrency(testCurrency);
		
		// Setup IncomeRequest
		incomeRequest = new IncomeRequest();
		incomeRequest.setName(testName);
		incomeRequest.setSource(testSource);
		incomeRequest.setMoney(moneyRequest);
		incomeRequest.setType(testType);
		incomeRequest.setMonth(testMonth);
		
		// Setup Income
		testIncome = new Income();
		testIncome.setId(testId);
		testIncome.setName(testName);
		testIncome.setSource(testSource);
		testIncome.setMoney(Money.of(testAmount, testCurrency));
		testIncome.setType(testType);
		testIncome.setMonth(testYearMonth);
	}
	
	// -- Create Income Tests --
	
	@Test
	void createIncome_ValidRequest_ReturnsCreated() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenReturn(testIncome);
		
		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(testName))
				.andExpect(jsonPath("$.source").value(testSource))
				.andExpect(jsonPath("$.money.amount").value(testAmount.toString()))
				.andExpect(jsonPath("$.money.currency").value(testCurrency.toString()))
				.andExpect(jsonPath("$.type").value(testType.toString()))
				.andExpect(jsonPath("$.month").value(testMonth));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	// -- Get Income Tests --
	
	@Test
	void getIncome_ValidId_ReturnsOk() throws Exception {
		Mockito.when(incomeService.getIncomeById(testId))
				.thenReturn(testIncome);
		
		mockMvc.perform(get("/api/incomes/{id}", testId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(testName))
				.andExpect(jsonPath("$.source").value(testSource))
				.andExpect(jsonPath("$.money.amount").value(testAmount.toString()))
				.andExpect(jsonPath("$.money.currency").value(testCurrency.toString()))
				.andExpect(jsonPath("$.type").value(testType.toString()))
				.andExpect(jsonPath("$.month").value(testMonth));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(testId);
	}
	
	@Test
	void getIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.getIncomeById(99L))
				.thenThrow(new IncomeNotFoundException("Income not found with id: 99"));

		mockMvc.perform(get("/api/incomes/{id}", 99L)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Income not found with id: 99"));

		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(99L);
	}
	
	// -- Get All Incomes Tests --
	
	@Test
	void getAllIncomes_ValidMonth_ReturnsOk() throws Exception {
		List<Income> incomeList = List.of(testIncome, testIncome);
		
		Mockito.when(incomeService.getAllIncomesForMonth(testMonth))
				.thenReturn(incomeList);
		
		mockMvc.perform(get("/api/incomes")
				.param("month", testMonth)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value(testName))
				.andExpect(jsonPath("$[1].name").value(testName));
				
		Mockito.verify(incomeService, Mockito.times(1))
				.getAllIncomesForMonth(testMonth);
	}
	
	// -- Update Income Tests --
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenReturn(testIncome);
		
		mockMvc.perform(put("/api/incomes/{id}", testId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(testName))
				.andExpect(jsonPath("$.source").value(testSource))
				.andExpect(jsonPath("$.money.amount").value(testAmount.toString()))
				.andExpect(jsonPath("$.money.currency").value(testCurrency.toString()))
				.andExpect(jsonPath("$.type").value(testType.toString()))
				.andExpect(jsonPath("$.month").value(testMonth));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenThrow(new IncomeNotFoundException("Income not found with id: 99"));
		
		mockMvc.perform(put("/api/incomes/{id}", 99L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Income not found with id: 99"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	// -- Delete Income Tests --
	
	@Test
	void deleteIncome_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(incomeService)
				.deleteIncome(testId);
		
		mockMvc.perform(delete("/api/incomes/{id}", testId))
				.andExpect(status().isNoContent());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(testId);
	}

	@Test
	void deleteIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new IncomeNotFoundException("Income not found with id: 99"))
				.when(incomeService)
				.deleteIncome(99L);
		
		mockMvc.perform(delete("/api/incomes/{id}", 99L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Income not found with id: 99"));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(99L);
	}
	
	// -- Error Handling Tests --
	
	@Test
	void createIncome_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new RuntimeException("Service failure"));

		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$").value("An unexpected error occurred."));

		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void createIncome_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new DataIntegrityViolationException("Database constraint violation"));
		
		mockMvc.perform(post("/api/incomes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$").value("A database constraint was violated."));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
}