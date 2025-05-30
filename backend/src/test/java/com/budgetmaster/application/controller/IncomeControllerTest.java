package com.budgetmaster.application.controller;

import java.util.List;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.service.IncomeService;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.builder.IncomeFactory;
import com.budgetmaster.testsupport.constants.Error;
import com.budgetmaster.testsupport.constants.Paths;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
		
		mockMvc.perform(post(Paths.Endpoints.INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonProperties.Income.NAME).value(IncomeConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonProperties.Income.SOURCE).value(IncomeConstants.Default.SOURCE))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONEY_AMOUNT).value(IncomeConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONEY_CURRENCY).value(IncomeConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(Paths.JsonProperties.Income.TYPE).value(IncomeConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonProperties.Income.YEAR).value(IncomeConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONTH).value(IncomeConstants.Default.MONTH));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void getIncome_ValidId_ReturnsOk() throws Exception {
		Mockito.when(incomeService.getIncomeById(IncomeConstants.Default.ID))
				.thenReturn(testIncome);
		
		mockMvc.perform(get(Paths.Endpoints.INCOME_WITH_ID, IncomeConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonProperties.Income.NAME).value(IncomeConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonProperties.Income.SOURCE).value(IncomeConstants.Default.SOURCE))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONEY_AMOUNT).value(IncomeConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONEY_CURRENCY).value(IncomeConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(Paths.JsonProperties.Income.TYPE).value(IncomeConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonProperties.Income.YEAR).value(IncomeConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONTH).value(IncomeConstants.Default.MONTH));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(IncomeConstants.Default.ID);
	}
	
	@Test
	void getIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.getIncomeById(IncomeConstants.NonExistent.ID))
				.thenThrow(new IncomeNotFoundException(String.format(Error.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));

		mockMvc.perform(get(Paths.Endpoints.INCOME_WITH_ID, IncomeConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(Paths.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.MESSAGE).value(String.format(Error.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
				.andExpect(jsonPath(Paths.JsonProperties.Error.PATH).value(String.format(Paths.Error.Income.URI_WITH_ID, IncomeConstants.NonExistent.ID)))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERRORS).isEmpty());

		Mockito.verify(incomeService, Mockito.times(1))
				.getIncomeById(IncomeConstants.NonExistent.ID);
	}
	
	@Test
	void getAllIncomes_ValidMonth_ReturnsOk() throws Exception {
		List<Income> incomeList = List.of(testIncome, testIncome);
		
		Mockito.when(incomeService.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH.toString()))
				.thenReturn(incomeList);
		
		mockMvc.perform(get(Paths.Endpoints.INCOME)
				.param(Paths.RequestParams.MONTH, IncomeConstants.Default.YEAR_MONTH.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonProperties.Income.FIRST_NAME).value(IncomeConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonProperties.Income.SECOND_NAME).value(IncomeConstants.Default.NAME));
				
		Mockito.verify(incomeService, Mockito.times(1))
				.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH.toString());
	}
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenReturn(testIncome);
		
		mockMvc.perform(put(Paths.Endpoints.INCOME_WITH_ID, IncomeConstants.Default.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath(Paths.JsonProperties.Income.NAME).value(IncomeConstants.Default.NAME))
				.andExpect(jsonPath(Paths.JsonProperties.Income.SOURCE).value(IncomeConstants.Default.SOURCE))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONEY_AMOUNT).value(IncomeConstants.Default.AMOUNT.toString()))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONEY_CURRENCY).value(IncomeConstants.Default.CURRENCY.toString()))
				.andExpect(jsonPath(Paths.JsonProperties.Income.TYPE).value(IncomeConstants.Default.TYPE.toString()))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONTH_YEAR).isArray())
				.andExpect(jsonPath(Paths.JsonProperties.Income.YEAR).value(IncomeConstants.Default.YEAR))
				.andExpect(jsonPath(Paths.JsonProperties.Income.MONTH).value(IncomeConstants.Default.MONTH));
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.when(incomeService.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class)))
				.thenThrow(new IncomeNotFoundException(String.format(Error.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));
		
		mockMvc.perform(put(Paths.Endpoints.INCOME_WITH_ID, IncomeConstants.NonExistent.ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(Paths.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.MESSAGE).value(String.format(Error.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
				.andExpect(jsonPath(Paths.JsonProperties.Error.PATH).value(String.format(Paths.Error.Income.URI_WITH_ID, IncomeConstants.NonExistent.ID)))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERRORS).isEmpty());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.updateIncome(Mockito.any(Long.class), Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void deleteIncome_ValidId_ReturnsNoContent() throws Exception {
		Mockito.doNothing()
				.when(incomeService)
				.deleteIncome(IncomeConstants.Default.ID);
		
		mockMvc.perform(delete(Paths.Endpoints.INCOME_WITH_ID, IncomeConstants.Default.ID))
				.andExpect(status().isNoContent());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(IncomeConstants.Default.ID);
	}

	@Test
	void deleteIncome_NonExistentId_ReturnsNotFound() throws Exception {
		Mockito.doThrow(new IncomeNotFoundException(String.format(Error.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
				.when(incomeService)
				.deleteIncome(IncomeConstants.NonExistent.ID);
		
		mockMvc.perform(delete(Paths.Endpoints.INCOME_WITH_ID, IncomeConstants.NonExistent.ID))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath(Paths.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(Paths.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.MESSAGE).value(String.format(Error.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
				.andExpect(jsonPath(Paths.JsonProperties.Error.PATH).value(String.format(Paths.Error.Income.URI_WITH_ID, IncomeConstants.NonExistent.ID)))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERRORS).isEmpty());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.deleteIncome(IncomeConstants.NonExistent.ID);
	}
	
	@Test
	void createIncome_ServiceError_ReturnsInternalServerError() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new RuntimeException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));

		mockMvc.perform(post(Paths.Endpoints.INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath(Paths.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(Paths.JsonProperties.Error.STATUS).value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERROR_CODE).value(ErrorCode.INTERNAL_SERVER_ERROR.name()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.MESSAGE).value(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.PATH).value(Paths.Error.Income.URI))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERRORS).isEmpty());

		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
	
	@Test
	void createIncome_DataIntegrityViolation_ReturnsConflict() throws Exception {
		Mockito.when(incomeService.createIncome(Mockito.any(IncomeRequest.class)))
				.thenThrow(new DataIntegrityViolationException(ErrorCode.DATABASE_ERROR.getMessage()));
		
		mockMvc.perform(post(Paths.Endpoints.INCOME)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(incomeRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath(Paths.JsonProperties.Error.TIMESTAMP).exists())
				.andExpect(jsonPath(Paths.JsonProperties.Error.STATUS).value(HttpStatus.CONFLICT.value()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERROR_CODE).value(ErrorCode.DATABASE_ERROR.name()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.MESSAGE).value(ErrorCode.DATABASE_ERROR.getMessage()))
				.andExpect(jsonPath(Paths.JsonProperties.Error.PATH).value(Paths.Error.Income.URI))
				.andExpect(jsonPath(Paths.JsonProperties.Error.ERRORS).isEmpty());
		
		Mockito.verify(incomeService, Mockito.times(1))
				.createIncome(Mockito.any(IncomeRequest.class));
	}
}