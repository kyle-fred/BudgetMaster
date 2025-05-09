package com.budgetmaster.income.controller;

import java.util.List;

import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.income.service.IncomeService;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestPaths;
import com.budgetmaster.test.constants.TestData.IncomeTestConstants;
import com.budgetmaster.test.factory.IncomeTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncomeController.class)
@Import(JacksonConfig.class)
public class IncomeControllerTest {
	// -- Dependencies --
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private IncomeService incomeService;
	
	// -- Test Objects --
	private Income testIncome;
	private IncomeRequest incomeRequest;
	
	@BeforeEach
	void setUp() {
		testIncome = IncomeTestFactory.createDefaultIncome();
		incomeRequest = IncomeTestFactory.createDefaultIncomeRequest();
	}
	
	@Test
	void createIncome_ValidRequest_ReturnsCreated() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenReturn(testIncome);
		
		mockMvc.perform(post(TestPaths.EndpointPathConstants.ENDPOINT_INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(IncomeTestConstants.Default.NAME))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_SOURCE).value(IncomeTestConstants.Default.SOURCE))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(IncomeTestConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(IncomeTestConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(IncomeTestConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_YEAR).value(IncomeTestConstants.Default.YEAR))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(IncomeTestConstants.Default.MONTH));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void getIncome_ValidId_ReturnsOk() throws Exception {
		Mockito.when(incomeService.getIncomeById(IncomeTestConstants.Default.ID))
				.thenReturn(testIncome);
		
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeTestConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(IncomeTestConstants.Default.NAME))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_SOURCE).value(IncomeTestConstants.Default.SOURCE))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(IncomeTestConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(IncomeTestConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(IncomeTestConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_YEAR).value(IncomeTestConstants.Default.YEAR))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(IncomeTestConstants.Default.MONTH));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(IncomeTestConstants.Default.ID);
	}
	
	@Test
	void getIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.getIncomeById(IncomeTestConstants.NonExistent.ID))
				.thenThrow(new IncomeNotFoundException(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeTestConstants.NonExistent.ID)));

		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeTestConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeTestConstants.NonExistent.ID)));

		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(IncomeTestConstants.NonExistent.ID);
	}
	
	@Test
	void getAllIncomes_ValidMonth_ReturnsOk() throws Exception {
		List<Income> incomeList = List.of(testIncome, testIncome);
		
		Mockito.when(incomeService.getAllIncomesForMonth(IncomeTestConstants.Default.YEAR_MONTH.toString()))
				.thenReturn(incomeList);
		
		mockMvc.perform(get(TestPaths.EndpointPathConstants.ENDPOINT_INCOME)
				.param(TestPaths.RequestParamsConstants.REQUEST_PARAM_MONTH, IncomeTestConstants.Default.YEAR_MONTH.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME_0).value(IncomeTestConstants.Default.NAME))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME_1).value(IncomeTestConstants.Default.NAME));
				
		Mockito.verify(incomeService, Mockito.times(1))
				.getAllIncomesForMonth(IncomeTestConstants.Default.YEAR_MONTH.toString());
	}
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenReturn(testIncome);
		
		mockMvc.perform(put(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeTestConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_NAME).value(IncomeTestConstants.Default.NAME))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_SOURCE).value(IncomeTestConstants.Default.SOURCE))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(IncomeTestConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(IncomeTestConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_TYPE).value(IncomeTestConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_YEAR).value(IncomeTestConstants.Default.YEAR))
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_MONTH).value(IncomeTestConstants.Default.MONTH));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenThrow(new IncomeNotFoundException(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeTestConstants.NonExistent.ID)));
		
		mockMvc.perform(put(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeTestConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeTestConstants.NonExistent.ID)));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void deleteIncome_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(incomeService)
				.deleteIncome(IncomeTestConstants.Default.ID);
		
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeTestConstants.Default.ID))
				.andExpect(status().isNoContent());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(IncomeTestConstants.Default.ID);
	}

	@Test
	void deleteIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new IncomeNotFoundException(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeTestConstants.NonExistent.ID)))
				.when(incomeService)
				.deleteIncome(IncomeTestConstants.NonExistent.ID);
		
		mockMvc.perform(delete(TestPaths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeTestConstants.NonExistent.ID))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(TestPaths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeTestConstants.NonExistent.ID)));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(IncomeTestConstants.NonExistent.ID);
	}
	
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