package com.budgetmaster.application.repository;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.budgetmaster.application.model.Expense;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.ExpenseIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.list.ExpenseIntegrationListAssertions;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

@Testcontainers
@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Expense Repository Tests")
class ExpenseRepositoryTest {

  @Autowired private ExpenseRepository expenseRepository;

  private Expense savedExpense;

  @BeforeEach
  void setUp() {
    expenseRepository.deleteAll();
    savedExpense = expenseRepository.save(ExpenseBuilder.defaultExpense().build());
  }

  @Nested
  @DisplayName("Expense Persistence")
  class ExpensePersistence {

    @Test
    @DisplayName("Should save expense to repository")
    void save_withValidExpense_persistsExpense() {
      ExpenseIntegrationAssertions.assertExpense(savedExpense).isDefaultExpense();
    }

    @Test
    @DisplayName("Should delete expense from repository")
    void delete_withExistingExpense_removesExpense() {
      expenseRepository.delete(savedExpense);

      ExpenseIntegrationAssertions.assertExpenseDeleted(savedExpense, expenseRepository);
    }
  }

  @Nested
  @DisplayName("Expense Retrieval")
  class ExpenseRetrieval {

    @Test
    @DisplayName("Should find expense by ID")
    void findById_withExistingId_returnsExpense() {
      Expense foundExpense = expenseRepository.findById(savedExpense.getId()).orElse(null);

      ExpenseIntegrationAssertions.assertExpense(foundExpense).isEqualTo(savedExpense);
    }

    @Test
    @DisplayName("Should find expenses by month")
    void findByMonth_withExistingMonth_returnsExpenses() {
      List<Expense> foundExpenses = expenseRepository.findByMonth(savedExpense.getMonth());

      ExpenseIntegrationListAssertions.assertExpenses(foundExpenses)
          .hasSize(1)
          .contains(savedExpense);
    }

    @Test
    @DisplayName("Should return empty list when no expenses found")
    void findByMonth_withNonExistentMonth_returnsEmptyList() {
      List<Expense> foundExpenses =
          expenseRepository.findByMonth(ExpenseConstants.NonExistent.YEAR_MONTH);

      ExpenseIntegrationListAssertions.assertExpenses(foundExpenses).hasSize(0);
    }
  }
}
