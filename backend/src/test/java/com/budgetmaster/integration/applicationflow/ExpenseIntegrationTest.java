package com.budgetmaster.integration.applicationflow;

import com.budgetmaster.application.controller.ExpenseController;
import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.application.exception.ExpenseNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.repository.ExpenseRepository;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.BudgetIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.ExpenseIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.list.ExpenseIntegrationListAssertions;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

@Testcontainers
@SpringBootTest
@Import(TestContainersConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Expense Integration Tests")
class ExpenseIntegrationTest {

    @Autowired
    private ExpenseController expenseController;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    private Expense savedExpense;
    private ExpenseRequest defaultExpenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        budgetRepository.deleteAll();
        savedExpense = expenseController.createExpense(defaultExpenseRequest).getBody();
    }

    @Nested
    @DisplayName("POST /expense Operations")
    class CreateExpenseOperations {
        
        @Test
        @DisplayName("Should create expense and synchronize budget when request is valid")
        void createExpense_withValidRequest_properlyPersistsAndSynchronizes() {
            ExpenseIntegrationAssertions.assertExpense(savedExpense)
                .isDefaultExpense();

            Expense persisted = expenseRepository.findById(savedExpense.getId()).orElse(null);
            ExpenseIntegrationAssertions.assertExpense(persisted)
                .isEqualTo(savedExpense);

            Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.Default.TOTAL_EXPENSE);
        }
    }

    @Nested
    @DisplayName("PUT /expense/{id} Operations")
    class UpdateExpenseOperations {
        
        @Test
        @DisplayName("Should update expense and synchronize budget when request is valid")
        void updateExpense_withValidRequest_properlyUpdatesAndSynchronizes() {
            ExpenseRequest updateRequest = ExpenseRequestBuilder.updatedExpenseRequest().buildRequest();

            Expense updatedExpense = expenseController.updateExpense(savedExpense.getId(), updateRequest).getBody();
            ExpenseIntegrationAssertions.assertExpense(updatedExpense)
                .hasId(savedExpense.getId())
                .isUpdatedExpense();

            Expense persisted = expenseRepository.findById(savedExpense.getId()).orElse(null);
            ExpenseIntegrationAssertions.assertExpense(persisted)
                .isEqualTo(updatedExpense);

            Budget oldBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(oldBudget)
                .hasTotalExpense(BudgetConstants.ZeroValues.TOTAL_EXPENSE);

            Budget newBudget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(newBudget)
                .hasTotalExpense(BudgetConstants.Updated.TOTAL_EXPENSE);
        }
    }

    @Nested
    @DisplayName("DELETE /expense/{id} Operations")
    class DeleteExpenseOperations {
        
        @Test
        @DisplayName("Should delete expense and synchronize budget when ID is valid")
        void deleteExpense_withValidId_properlyDeletesAndSynchronizes() {
            expenseController.deleteExpense(savedExpense.getId());
            ExpenseIntegrationAssertions.assertExpenseDeleted(savedExpense, expenseRepository);

            Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.ZeroValues.TOTAL_EXPENSE);
        }
    }

    @Nested
    @DisplayName("GET /expense Operations")
    class GetExpenseOperations {
        
        @Test
        @DisplayName("Should throw not found when month has no expenses")
        void getAllExpenses_withNonExistentMonth_throwsNotFoundException() {
            assertThatThrownBy(() -> expenseController.getAllExpensesForMonth(ExpenseConstants.NonExistent.YEAR_MONTH_STRING))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessageContaining(ExpenseConstants.NonExistent.YEAR_MONTH_STRING);
        }

        @Test
        @DisplayName("Should return list of expenses when month is valid")
        void getAllExpenses_withValidMonth_returnsCorrectList() {
            Expense secondExpense = expenseController.createExpense(defaultExpenseRequest).getBody();
            List<Expense> response = expenseController.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH_STRING).getBody();

            ExpenseIntegrationListAssertions.assertExpenses(response)
                .hasSize(2)
                .contains(savedExpense, secondExpense);
        }

        @Test
        @DisplayName("Should throw not found when ID does not exist")
        void getExpense_withNonExistentId_throwsNotFoundException() {
            assertThatThrownBy(() -> expenseController.getExpenseById(ExpenseConstants.NonExistent.ID))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessageContaining(ExpenseConstants.NonExistent.ID.toString());
        }
    }
} 