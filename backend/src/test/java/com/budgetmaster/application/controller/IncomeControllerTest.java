package com.budgetmaster.application.controller;

import java.util.List;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.service.IncomeService;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.PathConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncomeController.class)
@Import(JacksonConfig.class)
@DisplayName("Income Controller Tests")
class IncomeControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@SuppressWarnings("removal")
	@MockBean
	private IncomeService incomeService;
	
	private Income testIncome;
	private IncomeRequest incomeRequest;
	
	@BeforeEach
	void setUp() {
		testIncome = IncomeBuilder.defaultIncome().build();
		incomeRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();
	}

	@Nested
	@DisplayName("POST /income Operations")
	class CreateIncomeOperations {
		
		@Test
		@DisplayName("Should create income when request is valid")
		void createIncome_withValidRequest_returnsCreated() throws Exception {
			when(incomeService.createIncome(any(IncomeRequest.class)))
					.thenReturn(testIncome);
			
			mockMvc.perform(post(PathConstants.Endpoints.INCOME)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(incomeRequest)))
					.andExpect(status().isOk())
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.NAME).value(IncomeConstants.Default.NAME))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.SOURCE).value(IncomeConstants.Default.SOURCE))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONEY_AMOUNT).value(IncomeConstants.Default.AMOUNT.toString()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONEY_CURRENCY).value(IncomeConstants.Default.CURRENCY.toString()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.TYPE).value(IncomeConstants.Default.TYPE.toString()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONTH_YEAR).isArray())
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.YEAR).value(IncomeConstants.Default.YEAR))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONTH).value(IncomeConstants.Default.MONTH));
			
			verify(incomeService).createIncome(any(IncomeRequest.class));
		}

		@Test
		@DisplayName("Should return internal server error when service error occurs")
		void createIncome_withServiceError_returnsInternalServerError() throws Exception {
			when(incomeService.createIncome(any(IncomeRequest.class)))
					.thenThrow(new RuntimeException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));

			mockMvc.perform(post(PathConstants.Endpoints.INCOME)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(incomeRequest)))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.INTERNAL_SERVER_ERROR.name()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(PathConstants.Error.Income.URI))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());

			verify(incomeService).createIncome(any(IncomeRequest.class));
		}

		@Test
		@DisplayName("Should return conflict when data integrity violation occurs")
		void createIncome_withDataIntegrityViolation_returnsConflict() throws Exception {
			when(incomeService.createIncome(any(IncomeRequest.class)))
					.thenThrow(new DataIntegrityViolationException(ErrorCode.DATABASE_ERROR.getMessage()));
			
			mockMvc.perform(post(PathConstants.Endpoints.INCOME)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(incomeRequest)))
					.andExpect(status().isConflict())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.CONFLICT.value()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.DATABASE_ERROR.name()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(ErrorCode.DATABASE_ERROR.getMessage()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(PathConstants.Error.Income.URI))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());
			
			verify(incomeService).createIncome(any(IncomeRequest.class));
		}
	}

	@Nested
	@DisplayName("GET /income Operations")
	class GetIncomeOperations {
		
		@Test
		@DisplayName("Should return income when ID is valid")
		void getIncome_withValidId_returnsOk() throws Exception {
			when(incomeService.getIncomeById(IncomeConstants.Default.ID))
					.thenReturn(testIncome);
			
			mockMvc.perform(get(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.Default.ID)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.NAME).value(IncomeConstants.Default.NAME))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.SOURCE).value(IncomeConstants.Default.SOURCE))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONEY_AMOUNT).value(IncomeConstants.Default.AMOUNT.toString()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONEY_CURRENCY).value(IncomeConstants.Default.CURRENCY.toString()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.TYPE).value(IncomeConstants.Default.TYPE.toString()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONTH_YEAR).isArray())
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.YEAR).value(IncomeConstants.Default.YEAR))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONTH).value(IncomeConstants.Default.MONTH));
			
			verify(incomeService).getIncomeById(IncomeConstants.Default.ID);
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void getIncome_withNonExistentId_returnsNotFound() throws Exception {
			when(incomeService.getIncomeById(IncomeConstants.NonExistent.ID))
					.thenThrow(new IncomeNotFoundException(String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));

			mockMvc.perform(get(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.NonExistent.ID)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(String.format(PathConstants.Error.Income.URI_WITH_ID, IncomeConstants.NonExistent.ID)))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());

			verify(incomeService).getIncomeById(IncomeConstants.NonExistent.ID);
		}

		@Test
		@DisplayName("Should return all incomes when month is valid")
		void getAllIncomes_withValidMonth_returnsOk() throws Exception {
			List<Income> incomeList = List.of(testIncome, testIncome);
			
			when(incomeService.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(incomeList);
			
			mockMvc.perform(get(PathConstants.Endpoints.INCOME)
					.param(PathConstants.RequestParams.MONTH, IncomeConstants.Default.YEAR_MONTH.toString())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.FIRST_NAME).value(IncomeConstants.Default.NAME))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.SECOND_NAME).value(IncomeConstants.Default.NAME));
					
			verify(incomeService).getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH.toString());
		}
	}

	@Nested
	@DisplayName("PUT /income/{id} Operations")
	class UpdateIncomeOperations {
		
		@Test
		@DisplayName("Should update income when request is valid")
		void updateIncome_withValidRequest_returnsOk() throws Exception {
			when(incomeService.updateIncome(any(Long.class), any(IncomeRequest.class)))
					.thenReturn(testIncome);
			
			mockMvc.perform(put(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.Default.ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(incomeRequest)))
					.andExpect(status().isOk())
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.NAME).value(IncomeConstants.Default.NAME))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.SOURCE).value(IncomeConstants.Default.SOURCE))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONEY_AMOUNT).value(IncomeConstants.Default.AMOUNT.toString()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONEY_CURRENCY).value(IncomeConstants.Default.CURRENCY.toString()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.TYPE).value(IncomeConstants.Default.TYPE.toString()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONTH_YEAR).isArray())
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.YEAR).value(IncomeConstants.Default.YEAR))
					.andExpect(jsonPath(PathConstants.JsonProperties.Income.MONTH).value(IncomeConstants.Default.MONTH));
			
			verify(incomeService).updateIncome(any(Long.class), any(IncomeRequest.class));
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void updateIncome_withNonExistentId_returnsNotFound() throws Exception {
			when(incomeService.updateIncome(any(Long.class), any(IncomeRequest.class)))
					.thenThrow(new IncomeNotFoundException(String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));
			
			mockMvc.perform(put(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.NonExistent.ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(incomeRequest)))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(String.format(PathConstants.Error.Income.URI_WITH_ID, IncomeConstants.NonExistent.ID)))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());
			
			verify(incomeService).updateIncome(any(Long.class), any(IncomeRequest.class));
		}
	}

	@Nested
	@DisplayName("DELETE /income/{id} Operations")
	class DeleteIncomeOperations {
		
		@Test
		@DisplayName("Should delete income when ID is valid")
		void deleteIncome_withValidId_returnsNoContent() throws Exception {
			doNothing()
					.when(incomeService)
					.deleteIncome(IncomeConstants.Default.ID);
			
			mockMvc.perform(delete(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.Default.ID))
					.andExpect(status().isNoContent());
			
			verify(incomeService).deleteIncome(IncomeConstants.Default.ID);
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void deleteIncome_withNonExistentId_returnsNotFound() throws Exception {
			doThrow(new IncomeNotFoundException(String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
					.when(incomeService)
					.deleteIncome(IncomeConstants.NonExistent.ID);
			
			mockMvc.perform(delete(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.NonExistent.ID))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists())
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(HttpStatus.NOT_FOUND.value()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(ErrorCode.RESOURCE_NOT_FOUND.name()))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(String.format(PathConstants.Error.Income.URI_WITH_ID, IncomeConstants.NonExistent.ID)))
					.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());
			
			verify(incomeService).deleteIncome(IncomeConstants.NonExistent.ID);
		}
	}
}