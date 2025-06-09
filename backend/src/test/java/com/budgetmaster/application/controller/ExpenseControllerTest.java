package com.budgetmaster.application.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

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

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.application.exception.ExpenseNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.service.ExpenseService;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.assertions.controller.ExpenseControllerAssertions;
import com.budgetmaster.testsupport.assertions.controller.list.ExpenseControllerListAssertions;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.PathConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ExpenseController.class)
@Import(JacksonConfig.class)
@DisplayName("Expense Controller Tests")
class ExpenseControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @SuppressWarnings("removal")
  @MockBean
  private ExpenseService expenseService;

  private Expense defaultExpense;
  private Expense updatedExpense;
  private ExpenseRequest defaultExpenseRequest =
      ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();
  private ExpenseRequest updatedExpenseRequest =
      ExpenseRequestBuilder.updatedExpenseRequest().buildRequest();

  @BeforeEach
  void setUp() {
    defaultExpense = ExpenseBuilder.defaultExpense().build();
    updatedExpense = ExpenseBuilder.updatedExpense().build();
  }

  @Nested
  @DisplayName("POST /expense Operations")
  class CreateExpenseOperations {

    @Test
    @DisplayName("Should create expense when request is valid")
    void createExpense_withValidRequest_returnsCreated() throws Exception {
      when(expenseService.createExpense(any(ExpenseRequest.class))).thenReturn(defaultExpense);

      ResultActions validPostRequest =
          mockMvc.perform(
              post(PathConstants.Endpoints.EXPENSE)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(defaultExpenseRequest)));

      ExpenseControllerAssertions.assertThat(validPostRequest).isDefaultExpenseResponse();

      verify(expenseService).createExpense(any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("Should return internal server error when service error occurs")
    void createExpense_withServiceError_returnsInternalServerError() throws Exception {
      when(expenseService.createExpense(any(ExpenseRequest.class)))
          .thenThrow(new RuntimeException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));

      ResultActions serviceErrorRequest =
          mockMvc.perform(
              post(PathConstants.Endpoints.EXPENSE)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(defaultExpenseRequest)));

      ExpenseControllerAssertions.assertThat(serviceErrorRequest).isInternalServerError();

      verify(expenseService).createExpense(any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("Should return conflict when data integrity violation occurs")
    void createExpense_withDataIntegrityViolation_returnsConflict() throws Exception {
      when(expenseService.createExpense(any(ExpenseRequest.class)))
          .thenThrow(new DataIntegrityViolationException(ErrorCode.DATABASE_ERROR.getMessage()));

      ResultActions conflictRequest =
          mockMvc.perform(
              post(PathConstants.Endpoints.EXPENSE)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(defaultExpenseRequest)));

      ExpenseControllerAssertions.assertThat(conflictRequest).isConflict();

      verify(expenseService).createExpense(any(ExpenseRequest.class));
    }
  }

  @Nested
  @DisplayName("GET /expense Operations")
  class GetExpenseOperations {

    @Test
    @DisplayName("Should return expense when ID is valid")
    void getExpense_withValidId_returnsOk() throws Exception {
      when(expenseService.getExpenseById(ExpenseConstants.Default.ID)).thenReturn(defaultExpense);

      ResultActions validGetRequest =
          mockMvc.perform(
              get(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.Default.ID)
                  .contentType(MediaType.APPLICATION_JSON));

      ExpenseControllerAssertions.assertThat(validGetRequest).isDefaultExpenseResponse();

      verify(expenseService).getExpenseById(ExpenseConstants.Default.ID);
    }

    @Test
    @DisplayName("Should return not found when ID does not exist")
    void getExpense_withNonExistentId_returnsNotFound() throws Exception {
      when(expenseService.getExpenseById(ExpenseConstants.NonExistent.ID))
          .thenThrow(
              new ExpenseNotFoundException(
                  String.format(
                      ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)));

      ResultActions nonExistentIdGetRequest =
          mockMvc.perform(
              get(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.NonExistent.ID)
                  .contentType(MediaType.APPLICATION_JSON));

      ExpenseControllerAssertions.assertThat(nonExistentIdGetRequest)
          .isNotFoundForId(ExpenseConstants.NonExistent.ID);

      verify(expenseService).getExpenseById(ExpenseConstants.NonExistent.ID);
    }

    @Test
    @DisplayName("Should return all expenses when month is valid")
    void getAllExpenses_withValidMonth_returnsOk() throws Exception {
      Expense updatedExpense = ExpenseBuilder.updatedExpense().build();
      List<Expense> expenseList = List.of(defaultExpense, updatedExpense);

      when(expenseService.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString()))
          .thenReturn(expenseList);

      ResultActions validGetRequest =
          mockMvc.perform(
              get(PathConstants.Endpoints.EXPENSE)
                  .param(
                      PathConstants.RequestParams.MONTH,
                      ExpenseConstants.Default.YEAR_MONTH.toString())
                  .contentType(MediaType.APPLICATION_JSON));

      ExpenseControllerListAssertions.assertThat(validGetRequest)
          .hasSize(2)
          .next()
          .isDefaultExpense()
          .next()
          .isUpdatedExpense();

      verify(expenseService).getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH.toString());
    }
  }

  @Nested
  @DisplayName("PUT /expense/{id} Operations")
  class UpdateExpenseOperations {

    @Test
    @DisplayName("Should update expense when request is valid")
    void updateExpense_withValidRequest_returnsOk() throws Exception {
      when(expenseService.updateExpense(any(Long.class), any(ExpenseRequest.class)))
          .thenReturn(updatedExpense);

      ResultActions validPutRequest =
          mockMvc.perform(
              put(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.Default.ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updatedExpenseRequest)));

      ExpenseControllerAssertions.assertThat(validPutRequest).isUpdatedExpenseResponse();

      verify(expenseService).updateExpense(any(Long.class), any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("Should return not found when ID does not exist")
    void updateExpense_withNonExistentId_returnsNotFound() throws Exception {
      when(expenseService.updateExpense(any(Long.class), any(ExpenseRequest.class)))
          .thenThrow(
              new ExpenseNotFoundException(
                  String.format(
                      ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)));

      ResultActions nonExistentIdPutRequest =
          mockMvc.perform(
              put(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.NonExistent.ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updatedExpenseRequest)));

      ExpenseControllerAssertions.assertThat(nonExistentIdPutRequest)
          .isNotFoundForId(ExpenseConstants.NonExistent.ID);

      verify(expenseService).updateExpense(any(Long.class), any(ExpenseRequest.class));
    }
  }

  @Nested
  @DisplayName("DELETE /expense/{id} Operations")
  class DeleteExpenseOperations {

    @Test
    @DisplayName("Should delete expense when ID is valid")
    void deleteExpense_withValidId_returnsNoContent() throws Exception {
      doNothing().when(expenseService).deleteExpense(ExpenseConstants.Default.ID);

      ResultActions validDeleteRequest =
          mockMvc.perform(
              delete(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.Default.ID));

      ExpenseControllerAssertions.assertThat(validDeleteRequest).isNoContent();

      verify(expenseService).deleteExpense(ExpenseConstants.Default.ID);
    }

    @Test
    @DisplayName("Should return not found when ID does not exist")
    void deleteExpense_withNonExistentId_returnsNotFound() throws Exception {
      doThrow(
              new ExpenseNotFoundException(
                  String.format(
                      ErrorConstants.Expense.NOT_FOUND_WITH_ID, ExpenseConstants.NonExistent.ID)))
          .when(expenseService)
          .deleteExpense(ExpenseConstants.NonExistent.ID);

      ResultActions nonExistentIdDeleteRequest =
          mockMvc.perform(
              delete(PathConstants.Endpoints.EXPENSE_WITH_ID, ExpenseConstants.NonExistent.ID));

      ExpenseControllerAssertions.assertThat(nonExistentIdDeleteRequest)
          .isNotFoundForId(ExpenseConstants.NonExistent.ID);

      verify(expenseService).deleteExpense(ExpenseConstants.NonExistent.ID);
    }
  }
}
