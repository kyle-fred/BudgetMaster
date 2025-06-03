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
public class BudgetServiceIntegrationTest {
    
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

    @Test
    void getBudgetById_ExistingBudget_ReturnsCorrectBudget() {
        Budget persistedBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElseThrow();

        Budget budget = budgetService.getBudgetById(persistedBudget.getId());
        BudgetIntegrationAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME);
    }

    @Test
    void getBudgetByMonth_ExistingBudget_ReturnsCorrectBudget() {
        Budget budget = budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH_STRING);
        BudgetIntegrationAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME);
    }

    @Test
    void deleteBudget_ExistingBudget_DeletesSuccessfully() {
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElseThrow();

        budgetService.deleteBudget(budget.getId());
        BudgetIntegrationAssertions.assertBudgetDeleted(budget, budgetRepository);
    }

    @Test
    void getBudgetById_NonExistentId_ThrowsException() {
        assertThatThrownBy(() -> budgetService.getBudgetById(BudgetConstants.NonExistent.ID))
            .isInstanceOf(BudgetNotFoundException.class)
            .hasMessageContaining(BudgetConstants.NonExistent.ID.toString());
    }

    @Test
    void getBudgetByMonth_NonExistentMonth_ThrowsException() {
        assertThatThrownBy(() -> budgetService.getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH_STRING))
            .isInstanceOf(BudgetNotFoundException.class)
            .hasMessageContaining(BudgetConstants.NonExistent.YEAR_MONTH_STRING);
    }

    @Test
    void deleteBudget_NonExistentId_ThrowsException() {
        assertThatThrownBy(() -> budgetService.deleteBudget(BudgetConstants.NonExistent.ID))
            .isInstanceOf(BudgetNotFoundException.class)
            .hasMessageContaining(BudgetConstants.NonExistent.ID.toString());
    }
}
