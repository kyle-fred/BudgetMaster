package com.budgetmaster.income.controller;

import java.util.List;

import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.income.service.IncomeService;
import com.budgetmaster.testsupport.constants.Messages;
import com.budgetmaster.testsupport.constants.Paths;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
import com.budgetmaster.testsupport.income.factory.IncomeFactory;
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
		testIncome = IncomeFactory.createDefaultIncome();
		incomeRequest = IncomeFactory.createDefaultIncomeRequest();
	}
	
	@Test
	void createIncome_ValidRequest_ReturnsCreated() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenReturn(testIncome);
		
		mockMvc.perform(post(Paths.EndpointPathConstants.ENDPOINT_INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME).value(IncomeConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_SOURCE).value(IncomeConstants.Default.SOURCE))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(IncomeConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(IncomeConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_TYPE).value(IncomeConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_YEAR).value(IncomeConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH).value(IncomeConstants.Default.MONTH));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void getIncome_ValidId_ReturnsOk() throws Exception {
		Mockito.when(incomeService.getIncomeById(IncomeConstants.Default.ID))
				.thenReturn(testIncome);
		
		mockMvc.perform(get(Paths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME).value(IncomeConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_SOURCE).value(IncomeConstants.Default.SOURCE))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(IncomeConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(IncomeConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_TYPE).value(IncomeConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_YEAR).value(IncomeConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH).value(IncomeConstants.Default.MONTH));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(IncomeConstants.Default.ID);
	}
	
	@Test
	void getIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.getIncomeById(IncomeConstants.NonExistent.ID))
				.thenThrow(new IncomeNotFoundException(String.format(Messages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));

		mockMvc.perform(get(Paths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(Messages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));

		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(IncomeConstants.NonExistent.ID);
	}
	
	@Test
	void getAllIncomes_ValidMonth_ReturnsOk() throws Exception {
		List<Income> incomeList = List.of(testIncome, testIncome);
		
		Mockito.when(incomeService.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH.toString()))
				.thenReturn(incomeList);
		
		mockMvc.perform(get(Paths.EndpointPathConstants.ENDPOINT_INCOME)
				.param(Paths.RequestParamsConstants.REQUEST_PARAM_MONTH, IncomeConstants.Default.YEAR_MONTH.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME_0).value(IncomeConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME_1).value(IncomeConstants.Default.NAME));
				
		Mockito.verify(incomeService, Mockito.times(1))
				.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH.toString());
	}
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenReturn(testIncome);
		
		mockMvc.perform(put(Paths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_NAME).value(IncomeConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_SOURCE).value(IncomeConstants.Default.SOURCE))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_AMOUNT).value(IncomeConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONEY_CURRENCY).value(IncomeConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_TYPE).value(IncomeConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_YEAR).value(IncomeConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_MONTH).value(IncomeConstants.Default.MONTH));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenThrow(new IncomeNotFoundException(String.format(Messages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));
		
		mockMvc.perform(put(Paths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(Messages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void deleteIncome_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(incomeService)
				.deleteIncome(IncomeConstants.Default.ID);
		
		mockMvc.perform(delete(Paths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeConstants.Default.ID))
				.andExpect(status().isNoContent());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(IncomeConstants.Default.ID);
	}

	@Test
	void deleteIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new IncomeNotFoundException(String.format(Messages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
				.when(incomeService)
				.deleteIncome(IncomeConstants.NonExistent.ID);
		
		mockMvc.perform(delete(Paths.EndpointPathConstants.ENDPOINT_INCOME_WITH_ID, IncomeConstants.NonExistent.ID))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_ERROR).value(String.format(Messages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(IncomeConstants.NonExistent.ID);
	}
	
	@Test
	void createIncome_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new RuntimeException(Messages.CommonErrorMessageConstants.SERVICE_FAILURE));

		mockMvc.perform(post(Paths.EndpointPathConstants.ENDPOINT_INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_EMPTY).value(Messages.CommonErrorMessageConstants.UNEXPECTED_ERROR));

		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void createIncome_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new DataIntegrityViolationException(Messages.CommonErrorMessageConstants.DATABASE_CONSTRAINT));
		
		mockMvc.perform(post(Paths.EndpointPathConstants.ENDPOINT_INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath(Paths.JsonPathConstants.JSON_PATH_EMPTY).value(Messages.CommonErrorMessageConstants.DATABASE_CONSTRAINT));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
}