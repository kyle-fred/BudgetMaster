package com.budgetmaster.application.controller;

import java.util.List;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.service.IncomeService;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.assertions.controller.IncomeControllerAssertions;
import com.budgetmaster.testsupport.assertions.controller.list.IncomeControllerListAssertions;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
	
	private Income defaultIncome;
	private Income updatedIncome;
	private IncomeRequest defaultIncomeRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();
	private IncomeRequest updatedIncomeRequest = IncomeRequestBuilder.updatedIncomeRequest().buildRequest();
	
	@BeforeEach
	void setUp() {
		defaultIncome = IncomeBuilder.defaultIncome().build();
		updatedIncome = IncomeBuilder.updatedIncome().build();
	}

	@Nested
	@DisplayName("POST /income Operations")
	class CreateIncomeOperations {
		
		@Test
		@DisplayName("Should create income when request is valid")
		void createIncome_withValidRequest_returnsCreated() throws Exception {
			when(incomeService.createIncome(any(IncomeRequest.class)))
					.thenReturn(defaultIncome);
			
			ResultActions validPostRequest = mockMvc.perform(post(PathConstants.Endpoints.INCOME)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(defaultIncomeRequest)));

			IncomeControllerAssertions.assertThat(validPostRequest)
				.isDefaultIncomeResponse();
			
			verify(incomeService).createIncome(any(IncomeRequest.class));
		}

		@Test
		@DisplayName("Should return internal server error when service error occurs")
		void createIncome_withServiceError_returnsInternalServerError() throws Exception {
			when(incomeService.createIncome(any(IncomeRequest.class)))
					.thenThrow(new RuntimeException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));

			ResultActions serviceErrorRequest = mockMvc.perform(post(PathConstants.Endpoints.INCOME)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(defaultIncomeRequest)));
			
			IncomeControllerAssertions.assertThat(serviceErrorRequest)
				.isInternalServerError();

			verify(incomeService).createIncome(any(IncomeRequest.class));
		}

		@Test
		@DisplayName("Should return conflict when data integrity violation occurs")
		void createIncome_withDataIntegrityViolation_returnsConflict() throws Exception {
			when(incomeService.createIncome(any(IncomeRequest.class)))
					.thenThrow(new DataIntegrityViolationException(ErrorCode.DATABASE_ERROR.getMessage()));
			
			ResultActions conflictRequest = mockMvc.perform(post(PathConstants.Endpoints.INCOME)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(defaultIncomeRequest)));

			IncomeControllerAssertions.assertThat(conflictRequest)
				.isConflict();
			
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
					.thenReturn(defaultIncome);
			
			ResultActions validGetRequest = mockMvc.perform(get(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.Default.ID)
					.contentType(MediaType.APPLICATION_JSON));

			IncomeControllerAssertions.assertThat(validGetRequest)
				.isDefaultIncomeResponse();
			
			verify(incomeService).getIncomeById(IncomeConstants.Default.ID);
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void getIncome_withNonExistentId_returnsNotFound() throws Exception {
			when(incomeService.getIncomeById(IncomeConstants.NonExistent.ID))
					.thenThrow(new IncomeNotFoundException(String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));

			ResultActions nonExistentIdGetRequest = mockMvc.perform(get(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.NonExistent.ID)
					.contentType(MediaType.APPLICATION_JSON));
			
			IncomeControllerAssertions.assertThat(nonExistentIdGetRequest)
				.isNotFoundForId(IncomeConstants.NonExistent.ID);

			verify(incomeService).getIncomeById(IncomeConstants.NonExistent.ID);
		}

		@Test
		@DisplayName("Should return all incomes when month is valid")
		void getAllIncomes_withValidMonth_returnsOk() throws Exception {
			Income updatedIncome = IncomeBuilder.updatedIncome().build();
			List<Income> incomeList = List.of(defaultIncome, updatedIncome);
			
			when(incomeService.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(incomeList);
			
			ResultActions validGetRequest = mockMvc.perform(get(PathConstants.Endpoints.INCOME)
					.param(PathConstants.RequestParams.MONTH, IncomeConstants.Default.YEAR_MONTH.toString())
					.contentType(MediaType.APPLICATION_JSON));

			IncomeControllerListAssertions.assertThat(validGetRequest)
				.hasSize(2)
				.next().isDefaultIncome()
				.next().isUpdatedIncome();
					
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
					.thenReturn(updatedIncome);
			
			ResultActions validPutRequest = mockMvc.perform(put(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.Default.ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(updatedIncomeRequest)));

			IncomeControllerAssertions.assertThat(validPutRequest)
				.isUpdatedIncomeResponse();
			
			verify(incomeService).updateIncome(any(Long.class), any(IncomeRequest.class));
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void updateIncome_withNonExistentId_returnsNotFound() throws Exception {
			when(incomeService.updateIncome(any(Long.class), any(IncomeRequest.class)))
					.thenThrow(new IncomeNotFoundException(String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)));
			
			ResultActions nonExistentIdPutRequest = mockMvc.perform(put(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.NonExistent.ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(updatedIncomeRequest)));

			IncomeControllerAssertions.assertThat(nonExistentIdPutRequest)
				.isNotFoundForId(IncomeConstants.NonExistent.ID);
			
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
			
			ResultActions validDeleteRequest = mockMvc.perform(delete(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.Default.ID));

			IncomeControllerAssertions.assertThat(validDeleteRequest)
				.isNoContent();
			
			verify(incomeService).deleteIncome(IncomeConstants.Default.ID);
		}

		@Test
		@DisplayName("Should return not found when ID does not exist")
		void deleteIncome_withNonExistentId_returnsNotFound() throws Exception {
			doThrow(new IncomeNotFoundException(String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID)))
					.when(incomeService)
					.deleteIncome(IncomeConstants.NonExistent.ID);
			
			ResultActions nonExistentIdDeleteRequest = mockMvc.perform(delete(PathConstants.Endpoints.INCOME_WITH_ID, IncomeConstants.NonExistent.ID));

			IncomeControllerAssertions.assertThat(nonExistentIdDeleteRequest)
				.isNotFoundForId(IncomeConstants.NonExistent.ID);
			
			verify(incomeService).deleteIncome(IncomeConstants.NonExistent.ID);
		}
	}
}