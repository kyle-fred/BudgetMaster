package com.budgetmaster.integration.service;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.application.service.BudgetService;
import com.budgetmaster.application.service.IncomeService;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.BudgetIntegrationAssertions;
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
@DisplayName("Budget Service Integration Tests")
class BudgetServiceIntegrationTest {
    
    @Autowired
    private BudgetService budgetService;

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    private IncomeRequest defaultIncomeRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();

    @BeforeEach
    void setUp() {
        incomeRepository.deleteAll();
        budgetRepository.deleteAll();
        incomeService.createIncome(defaultIncomeRequest);
    }

    @Nested
    @DisplayName("Get Budget Operations")
    class GetBudgetOperations {
        
        @Test
        @DisplayName("Should return budget when ID is valid")
        void getBudget_withValidId_returnsBudget() {
            Budget persistedBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElseThrow();

            Budget budget = budgetService.getBudgetById(persistedBudget.getId());
            BudgetIntegrationAssertions.assertBudget(budget)
                .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME);
        }

        @Test
        @DisplayName("Should return budget when month is valid")
        void getBudget_withValidMonth_returnsBudget() {
            Budget budget = budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH_STRING);
            BudgetIntegrationAssertions.assertBudget(budget)
                .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME);
        }

        @Test
        @DisplayName("Should throw not found when ID does not exist")
        void getBudget_withNonExistentId_throwsNotFoundException() {
            assertThatThrownBy(() -> budgetService.getBudgetById(BudgetConstants.NonExistent.ID))
                .isInstanceOf(BudgetNotFoundException.class)
                .hasMessageContaining(BudgetConstants.NonExistent.ID.toString());
        }

        @Test
        @DisplayName("Should throw not found when month does not correspond to a budget")
        void getBudget_withNonExistentMonth_throwsNotFoundException() {
            assertThatThrownBy(() -> budgetService.getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH_STRING))
                .isInstanceOf(BudgetNotFoundException.class)
                .hasMessageContaining(BudgetConstants.NonExistent.YEAR_MONTH_STRING);
        }
    }

    @Nested
    @DisplayName("Delete Budget Operations")
    class DeleteBudgetOperations {
        
        @Test
        @DisplayName("Should delete budget when ID is valid")
        void deleteBudget_withValidId_deletesBudget() {
            Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElseThrow();

            budgetService.deleteBudget(budget.getId());
            BudgetIntegrationAssertions.assertBudgetDeleted(budget, budgetRepository);
        }

        @Test
        @DisplayName("Should throw not found when ID does not exist")
        void deleteBudget_withNonExistentId_throwsNotFoundException() {
            assertThatThrownBy(() -> budgetService.deleteBudget(BudgetConstants.NonExistent.ID))
                .isInstanceOf(BudgetNotFoundException.class)
                .hasMessageContaining(BudgetConstants.NonExistent.ID.toString());
        }
    }
}
