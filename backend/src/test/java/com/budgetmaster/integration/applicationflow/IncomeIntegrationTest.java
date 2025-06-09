package com.budgetmaster.integration.applicationflow;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.budgetmaster.application.controller.IncomeController;
import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.BudgetIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.IncomeIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.list.IncomeIntegrationListAssertions;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

@Testcontainers
@SpringBootTest
@Import(TestContainersConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Income Integration Tests")
class IncomeIntegrationTest {

  @Autowired private IncomeController incomeController;

  @Autowired private IncomeRepository incomeRepository;

  @Autowired private BudgetRepository budgetRepository;

  private Income savedIncome;
  private IncomeRequest defaultIncomeRequest =
      IncomeRequestBuilder.defaultIncomeRequest().buildRequest();

  @BeforeEach
  void setUp() {
    incomeRepository.deleteAll();
    budgetRepository.deleteAll();
    savedIncome = incomeController.createIncome(defaultIncomeRequest).getBody();
  }

  @Nested
  @DisplayName("POST /income Operations")
  class CreateIncomeOperations {

    @Test
    @DisplayName("Should create income and synchronize budget when request is valid")
    void createIncome_withValidRequest_properlyPersistsAndSynchronizes() {
      IncomeIntegrationAssertions.assertIncome(savedIncome).isDefaultIncome();

      Income persisted = incomeRepository.findById(savedIncome.getId()).orElse(null);
      IncomeIntegrationAssertions.assertIncome(persisted).isEqualTo(savedIncome);

      Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
      BudgetIntegrationAssertions.assertBudget(budget)
          .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME);
    }
  }

  @Nested
  @DisplayName("PUT /income/{id} Operations")
  class UpdateIncomeOperations {

    @Test
    @DisplayName("Should update income and synchronize budget when request is valid")
    void updateIncome_withValidRequest_properlyUpdatesAndSynchronizes() {
      IncomeRequest updateRequest = IncomeRequestBuilder.updatedIncomeRequest().buildRequest();

      Income updatedIncome =
          incomeController.updateIncome(savedIncome.getId(), updateRequest).getBody();
      IncomeIntegrationAssertions.assertIncome(updatedIncome)
          .hasId(savedIncome.getId())
          .isUpdatedIncome();

      Income persisted = incomeRepository.findById(savedIncome.getId()).orElse(null);
      IncomeIntegrationAssertions.assertIncome(persisted).isEqualTo(updatedIncome);

      Budget oldBudget =
          budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
      BudgetIntegrationAssertions.assertBudget(oldBudget)
          .hasTotalIncome(BudgetConstants.ZeroValues.TOTAL_INCOME);

      Budget newBudget =
          budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
      BudgetIntegrationAssertions.assertBudget(newBudget)
          .hasTotalIncome(BudgetConstants.Updated.TOTAL_INCOME);
    }
  }

  @Nested
  @DisplayName("DELETE /income/{id} Operations")
  class DeleteIncomeOperations {

    @Test
    @DisplayName("Should delete income and synchronize budget when ID is valid")
    void deleteIncome_withValidId_properlyDeletesAndSynchronizes() {
      incomeController.deleteIncome(savedIncome.getId());
      IncomeIntegrationAssertions.assertIncomeDeleted(savedIncome, incomeRepository);

      Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
      BudgetIntegrationAssertions.assertBudget(budget)
          .hasTotalIncome(BudgetConstants.ZeroValues.TOTAL_INCOME);
    }
  }

  @Nested
  @DisplayName("GET /income Operations")
  class GetIncomeOperations {

    @Test
    @DisplayName("Should throw not found when month has no incomes")
    void getAllIncomes_withNonExistentMonth_throwsNotFoundException() {
      assertThatThrownBy(
              () ->
                  incomeController.getAllIncomesForMonth(
                      IncomeConstants.NonExistent.YEAR_MONTH_STRING))
          .isInstanceOf(IncomeNotFoundException.class)
          .hasMessageContaining(IncomeConstants.NonExistent.YEAR_MONTH_STRING);
    }

    @Test
    @DisplayName("Should return list of incomes when month is valid")
    void getAllIncomes_withValidMonth_returnsCorrectList() {
      Income secondIncome = incomeController.createIncome(defaultIncomeRequest).getBody();
      List<Income> response =
          incomeController
              .getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH_STRING)
              .getBody();

      IncomeIntegrationListAssertions.assertIncomes(response)
          .hasSize(2)
          .contains(savedIncome, secondIncome);
    }

    @Test
    @DisplayName("Should throw not found when ID does not exist")
    void getIncome_withNonExistentId_throwsNotFoundException() {
      assertThatThrownBy(() -> incomeController.getIncomeById(IncomeConstants.NonExistent.ID))
          .isInstanceOf(IncomeNotFoundException.class)
          .hasMessageContaining(IncomeConstants.NonExistent.ID.toString());
    }
  }
}
