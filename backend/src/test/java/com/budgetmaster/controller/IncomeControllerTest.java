package com.budgetmaster.controller;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.income.controller.IncomeController;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.income.service.IncomeService;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestPaths;

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
	private static final Long testId = TestData.CommonTestDataConstants.ID_EXISTING;
	private static final Long testNonExistentId = TestData.CommonTestDataConstants.ID_NON_EXISTING;
	private static final String testName = TestData.IncomeTestDataConstants.NAME;
	private static final String testSource = TestData.IncomeTestDataConstants.SOURCE;
	private static final BigDecimal testAmount = TestData.IncomeTestDataConstants.AMOUNT;
	private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
	private static final TransactionType testType = TestData.IncomeTestDataConstants.TYPE_ONE_TIME;
	private static final String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;
	private static final YearMonth testYearMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
	
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
		
		mockMvc.perform(post(TestPaths.EndpointPathConstants.ENDPOINT_INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_SOURCE).value(testSource))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(testMonth));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	// -- Get Income Tests --
	
	@Test
	void getIncome_ValidId_ReturnsOk() throws Exception {
		Mockito.when(incomeService.getIncomeById(testId))
				.thenReturn(testIncome);
		
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, testId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_SOURCE).value(testSource))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(testMonth));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(testId);
	}
	
	@Test
	void getIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.getIncomeById(testNonExistentId))
				.thenThrow(new IncomeNotFoundException(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));

		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, testNonExistentId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));

		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(testNonExistentId);
	}
	
	// -- Get All Incomes Tests --
	
	@Test
	void getAllIncomes_ValidMonth_ReturnsOk() throws Exception {
		List<Income> incomeList = List.of(testIncome, testIncome);
		
		Mockito.when(incomeService.getAllIncomesForMonth(testMonth))
				.thenReturn(incomeList);
		
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_INCOME)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, testMonth)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME_0).value(testName))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME_1).value(testName));
				
		Mockito.verify(incomeService, Mockito.times(1))
				.getAllIncomesForMonth(testMonth);
	}
	
	// -- Update Income Tests --
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenReturn(testIncome);
		
		mockMvc.perform(put(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, testId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_SOURCE).value(testSource))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(testMonth));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenThrow(new IncomeNotFoundException(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));
		
		mockMvc.perform(put(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, testNonExistentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	// -- Delete Income Tests --
	
	@Test
	void deleteIncome_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(incomeService)
				.deleteIncome(testId);
		
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, testId))
				.andExpect(status().isNoContent());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(testId);
	}

	@Test
	void deleteIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new IncomeNotFoundException(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, testNonExistentId)))
				.when(incomeService)
				.deleteIncome(testNonExistentId);
		
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, testNonExistentId))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(testNonExistentId);
	}
	
	// -- Error Handling Tests --
	
	@Test
	void createIncome_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new RuntimeException(TestMessages.CommonErrorMessageConstants.SERVICE_FAILURE));

		mockMvc.perform(post(TestPaths.EndpointPathConstants.ENDPOINT_INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_EMPTY).value(TestMessages.CommonErrorMessageConstants.UNEXPECTED_ERROR));

		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void createIncome_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new DataIntegrityViolationException(TestMessages.CommonErrorMessageConstants.DATABASE_CONSTRAINT));
		
		mockMvc.perform(post(TestPaths.EndpointPathConstants.ENDPOINT_INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_EMPTY).value(TestMessages.CommonErrorMessageConstants.DATABASE_CONSTRAINT));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
}