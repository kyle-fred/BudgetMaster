package com.budgetmaster.integration.service;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.application.exception.ExpenseNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.repository.ExpenseRepository;
import com.budgetmaster.application.service.ExpenseService;
import com.budgetmaster.application.service.synchronization.ExpenseBudgetSynchronizer;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@Testcontainers
@SpringBootTest
@Import(TestContainersConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Expense Service Integration Tests")
class ExpenseServiceIntegrationTest {

    @SuppressWarnings("removal")
    @SpyBean
    private ExpenseBudgetSynchronizer expenseBudgetSynchronizer;

    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    private ExpenseRequest defaultRequest;
    
    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        budgetRepository.deleteAll();
        defaultRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();
    }
    
    @Nested
    @DisplayName("Create Expense Operations")
    class CreateExpenseOperations {
        
        @Test
        @DisplayName("Should create expense and synchronize budget when request is valid")
        void createExpense_withValidRequest_properlyPersistsAndSynchronizes() {
            Expense createdExpense = expenseService.createExpense(defaultRequest);
            
            ExpenseIntegrationAssertions.assertExpense(createdExpense)
                .isDefaultExpense();
            
            Expense persisted = expenseRepository.findById(createdExpense.getId()).orElse(null);
            ExpenseIntegrationAssertions.assertExpense(persisted)
                .isEqualTo(createdExpense);
            
            Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.Default.TOTAL_EXPENSE);
        }

        @Test
        @DisplayName("Should rollback transaction when synchronization fails")
        void createExpense_withSynchronizationFailure_rollsBackTransaction() {
            Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
                    .when(expenseBudgetSynchronizer).apply(any(Expense.class));

            assertThatThrownBy(() -> expenseService.createExpense(defaultRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

            ExpenseIntegrationListAssertions.assertExpenses(expenseRepository.findAll())
                .hasSize(0);

            Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudgetNotInitialized(budget);
        }
    }

    @Nested
    @DisplayName("Update Expense Operations")
    class UpdateExpenseOperations {
        
        @Test
        @DisplayName("Should update expense and synchronize budget when request is valid")
        void updateExpense_withValidRequest_properlyUpdatesAndSynchronizes() {
            Expense createdExpense = expenseService.createExpense(defaultRequest);
            ExpenseRequest updateRequest = ExpenseRequestBuilder.updatedExpenseRequest().buildRequest();
            
            Expense updatedExpense = expenseService.updateExpense(createdExpense.getId(), updateRequest);
            ExpenseIntegrationAssertions.assertExpense(updatedExpense)
                .hasId(createdExpense.getId())
                .isUpdatedExpense();
            
            Expense persisted = expenseRepository.findById(createdExpense.getId()).orElse(null);
            ExpenseIntegrationAssertions.assertExpense(persisted)
                .isEqualTo(updatedExpense);
            
            Budget budget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.Updated.TOTAL_EXPENSE);
        }

        @Test
        @DisplayName("Should synchronize both budgets when month changes")
        void updateExpense_withMonthChange_properlySynchronizesBothBudgets() {
            Expense createdExpense = expenseService.createExpense(defaultRequest);
            ExpenseRequest updateRequest = ExpenseRequestBuilder.updatedExpenseRequest().buildRequest();
            expenseService.updateExpense(createdExpense.getId(), updateRequest);
            
            Budget oldBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(oldBudget)
                .hasTotalExpense(BudgetConstants.ZeroValues.TOTAL_EXPENSE);
            
            Budget newBudget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(newBudget)
                .hasTotalExpense(BudgetConstants.Updated.TOTAL_EXPENSE);
        }

        @Test
        @DisplayName("Should rollback transaction when synchronization fails")
        void updateExpense_withSynchronizationFailure_rollsBackTransaction() {
            Expense original = expenseService.createExpense(defaultRequest);
            ExpenseRequest updatedRequest = ExpenseRequestBuilder.updatedExpenseRequest().buildRequest();
            
            Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
                .when(expenseBudgetSynchronizer).reapply(any(Expense.class), any(Expense.class));

            assertThatThrownBy(() -> expenseService.updateExpense(original.getId(), updatedRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

            Expense persisted = expenseRepository.findById(original.getId()).orElse(null);
            ExpenseIntegrationAssertions.assertExpense(persisted)
                .isEqualTo(original);

            Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.Default.TOTAL_EXPENSE);
        }
    }

    @Nested
    @DisplayName("Delete Expense Operations")
    class DeleteExpenseOperations {
        
        @Test
        @DisplayName("Should delete expense and synchronize budget when ID is valid")
        void deleteExpense_withValidId_properlyDeletesAndSynchronizes() {
            Expense createdExpense = expenseService.createExpense(defaultRequest);
            expenseService.deleteExpense(createdExpense.getId());
            ExpenseIntegrationAssertions.assertExpenseDeleted(createdExpense, expenseRepository);
            
            Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.ZeroValues.TOTAL_EXPENSE);
        }

        @Test
        @DisplayName("Should rollback transaction when synchronization fails")
        void deleteExpense_withSynchronizationFailure_rollsBackTransaction() {
            Expense expense = expenseService.createExpense(defaultRequest);
            
            Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
                .when(expenseBudgetSynchronizer).retract(any(Expense.class));

            assertThatThrownBy(() -> expenseService.deleteExpense(expense.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

            ExpenseIntegrationAssertions.assertExpense(expense)
                .isEqualTo(expense);

            Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
            BudgetIntegrationAssertions.assertBudget(budget)
                .hasTotalExpense(BudgetConstants.Default.TOTAL_EXPENSE);
        }
    }

    @Nested
    @DisplayName("Get Expense Operations")
    class GetExpenseOperations {
        
        @Test
        @DisplayName("Should throw not found when ID does not exist")
        void getExpense_withNonExistentId_throwsNotFoundException() {
            assertThatThrownBy(() -> expenseService.getExpenseById(ExpenseConstants.NonExistent.ID))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessageContaining(ExpenseConstants.NonExistent.ID.toString());
        }
        
        @Test
        @DisplayName("Should throw not found when month has no expenses")
        void getAllExpenses_withNonExistentMonth_throwsNotFoundException() {
            assertThatThrownBy(() -> expenseService.getAllExpensesForMonth(ExpenseConstants.NonExistent.YEAR_MONTH_STRING))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessageContaining(ExpenseConstants.NonExistent.YEAR_MONTH_STRING);
        }
    }
}
