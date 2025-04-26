package com.budgetmaster.controller;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.SupportedCurrency;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.IncomeNotFoundException;
import com.budgetmaster.service.IncomeService;
import com.budgetmaster.test.constants.TestData.IncomeTestData;
import com.budgetmaster.test.constants.TestData.SharedTestData;
import com.budgetmaster.test.constants.TestMessages.CommonErrorMessages;
import com.budgetmaster.test.constants.TestMessages.IncomeErrorMessages;
import com.budgetmaster.test.constants.TestPaths.TestIncomePaths;
import com.budgetmaster.test.constants.TestPaths.TestJsonPaths;
import com.budgetmaster.test.constants.TestPaths.TestRequestParams;
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
	private static final Long testId = SharedTestData.TEST_EXISTING_ID;
	private static final Long testNonExistentId = SharedTestData.TEST_NON_EXISTING_ID;
	private static final String testName = IncomeTestData.TEST_NAME;
	private static final String testSource = IncomeTestData.TEST_SOURCE;
	private static final BigDecimal testAmount = IncomeTestData.TEST_AMOUNT;
	private static final Currency testCurrency = SupportedCurrency.GBP.getCurrency();
	private static final TransactionType testType = TransactionType.ONE_TIME;
	private static final String testMonth = SharedTestData.TEST_EXISTING_MONTH_YEAR_STRING;
	private static final YearMonth testYearMonth = SharedTestData.TEST_EXISTING_MONTH_YEAR;
	
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
		
		mockMvc.perform(post(TestIncomePaths.TEST_INCOME_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_SOURCE).value(testSource))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONTH).value(testMonth));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	// -- Get Income Tests --
	
	@Test
	void getIncome_ValidId_ReturnsOk() throws Exception {
		Mockito.when(incomeService.getIncomeById(testId))
				.thenReturn(testIncome);
		
		mockMvc.perform(get(TestIncomePaths.TEST_INCOME_ENDPOINT_WITH_ID, testId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_SOURCE).value(testSource))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONTH).value(testMonth));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(testId);
	}
	
	@Test
	void getIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.getIncomeById(testNonExistentId))
				.thenThrow(new IncomeNotFoundException(String.format(IncomeErrorMessages.TEST_MESSAGE_INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));

		mockMvc.perform(get(TestIncomePaths.TEST_INCOME_ENDPOINT_WITH_ID, testNonExistentId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_ERROR).value(String.format(IncomeErrorMessages.TEST_MESSAGE_INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));

		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(testNonExistentId);
	}
	
	// -- Get All Incomes Tests --
	
	@Test
	void getAllIncomes_ValidMonth_ReturnsOk() throws Exception {
		List<Income> incomeList = List.of(testIncome, testIncome);
		
		Mockito.when(incomeService.getAllIncomesForMonth(testMonth))
				.thenReturn(incomeList);
		
		mockMvc.perform(get(TestIncomePaths.TEST_INCOME_ENDPOINT)
				.param(TestRequestParams.TEST_PARAM_MONTH, testMonth)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME_0).value(testName))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME_1).value(testName));
				
		Mockito.verify(incomeService, Mockito.times(1))
				.getAllIncomesForMonth(testMonth);
	}
	
	// -- Update Income Tests --
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenReturn(testIncome);
		
		mockMvc.perform(put(TestIncomePaths.TEST_INCOME_ENDPOINT_WITH_ID, testId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_NAME).value(testName))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_SOURCE).value(testSource))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_AMOUNT).value(testAmount.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONEY_CURRENCY).value(testCurrency.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_TYPE).value(testType.toString()))
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_MONTH).value(testMonth));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenThrow(new IncomeNotFoundException(String.format(IncomeErrorMessages.TEST_MESSAGE_INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));
		
		mockMvc.perform(put(TestIncomePaths.TEST_INCOME_ENDPOINT_WITH_ID, testNonExistentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_ERROR).value(String.format(IncomeErrorMessages.TEST_MESSAGE_INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	// -- Delete Income Tests --
	
	@Test
	void deleteIncome_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(incomeService)
				.deleteIncome(testId);
		
		mockMvc.perform(delete(TestIncomePaths.TEST_INCOME_ENDPOINT_WITH_ID, testId))
				.andExpect(status().isNoContent());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(testId);
	}

	@Test
	void deleteIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new IncomeNotFoundException(String.format(IncomeErrorMessages.TEST_MESSAGE_INCOME_NOT_FOUND_WITH_ID, testNonExistentId)))
				.when(incomeService)
				.deleteIncome(testNonExistentId);
		
		mockMvc.perform(delete(TestIncomePaths.TEST_INCOME_ENDPOINT_WITH_ID, testNonExistentId))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_ERROR).value(String.format(IncomeErrorMessages.TEST_MESSAGE_INCOME_NOT_FOUND_WITH_ID, testNonExistentId)));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(testNonExistentId);
	}
	
	// -- Error Handling Tests --
	
	@Test
	void createIncome_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new RuntimeException(CommonErrorMessages.TEST_MESSAGE_SERVICE_FAILURE));

		mockMvc.perform(post(TestIncomePaths.TEST_INCOME_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_EMPTY).value(CommonErrorMessages.TEST_MESSAGE_INTERNAL_SERVER_ERROR));

		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void createIncome_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new DataIntegrityViolationException(CommonErrorMessages.TEST_MESSAGE_DATABASE_CONSTRAINT_VIOLATION));
		
		mockMvc.perform(post(TestIncomePaths.TEST_INCOME_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath(TestJsonPaths.TEST_JSON_PATH_EMPTY).value(CommonErrorMessages.TEST_MESSAGE_DATABASE_CONSTRAINT_VIOLATION));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
}