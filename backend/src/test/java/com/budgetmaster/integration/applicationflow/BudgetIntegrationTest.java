package com.budgetmaster.integration.applicationflow;

import com.budgetmaster.application.controller.BudgetController;
import com.budgetmaster.application.controller.ExpenseController;
import com.budgetmaster.application.controller.IncomeController;
import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.BudgetIntegrationAssertions;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

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

@Testcontainers
@SpringBootTest
@Import(TestContainersConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Budget Integration Tests")
class BudgetIntegrationTest {
    
    @Autowired
    private BudgetController budgetController;

    @Autowired
    private IncomeController incomeController;

    @Autowired
    private ExpenseController expenseController;

    @Autowired
    private BudgetRepository budgetRepository;

    @BeforeEach
    void setUp() {
        budgetRepository.deleteAll();
        incomeController.createIncome(IncomeRequestBuilder.defaultIncomeRequest().buildRequest());
        expenseController.createExpense(ExpenseRequestBuilder.defaultExpenseRequest().buildRequest());
    }

    @Nested
    @DisplayName("GET /budget Operations")
    class GetBudgetOperations {
        
        @Test
        @DisplayName("Should return budget when month is valid")
        void getBudget_withValidMonth_returnsBudget() {
            Budget budget = budgetController.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH_STRING).getBody();
            BudgetIntegrationAssertions.assertBudget(budget)
                .isDefaultBudget();
        }

        @Test
        @DisplayName("Should throw not found when month does not correspond to a budget")
        void getBudget_withNonExistentMonth_throwsNotFoundException() {
            assertThatThrownBy(() -> budgetController.getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH_STRING))
                .isInstanceOf(BudgetNotFoundException.class)
                .hasMessageContaining(BudgetConstants.NonExistent.YEAR_MONTH_STRING);
        }
    }

    @Nested
    @DisplayName("DELETE /budget/{id} Operations")
    class DeleteBudgetOperations {
        
        @Test
        @DisplayName("Should delete budget when ID is valid")
        @SuppressWarnings("null") // We are explicitly testing validation error handling which may involve nulls
        void deleteBudget_withValidId_deletesBudget() {
            Budget budget = budgetController.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH_STRING).getBody();

            budgetController.deleteBudget(budget.getId());
            BudgetIntegrationAssertions.assertBudgetDeleted(budget, budgetRepository);
        }

        @Test
        @DisplayName("Should throw not found when ID does not exist")
        void deleteBudget_withNonExistentId_throwsNotFoundException() {
            assertThatThrownBy(() -> budgetController.deleteBudget(BudgetConstants.NonExistent.ID))
                .isInstanceOf(BudgetNotFoundException.class)
                .hasMessageContaining(BudgetConstants.NonExistent.ID.toString());
        }
    }
} 