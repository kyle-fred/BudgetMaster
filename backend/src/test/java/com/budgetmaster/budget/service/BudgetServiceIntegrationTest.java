package com.budgetmaster.budget.service;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.config.TestContainersConfig;
import com.budgetmaster.income.repository.IncomeRepository;
import com.budgetmaster.income.service.IncomeService;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.income.factory.IncomeFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
@Import(TestContainersConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BudgetServiceIntegrationTest {
    // -- Dependencies --
    @Autowired
    private BudgetService budgetService;

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    // -- Test Objects --
    // private Income income;

    @BeforeEach
    void setUp() {
        incomeRepository.deleteAll();
        budgetRepository.deleteAll();
        incomeService.createIncome(IncomeFactory.createDefaultIncomeRequest());
    }

    // -- Happy Path Tests --

    @Test
    void getBudgetById_ExistingBudget_ReturnsCorrectBudget() {
        Budget persistedBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElseThrow();
        Budget result = budgetService.getBudgetById(persistedBudget.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(persistedBudget.getId());
        assertThat(result.getMonth()).isEqualTo(BudgetConstants.Default.YEAR_MONTH);
    }

    @Test
    void getBudgetByMonth_ExistingBudget_ReturnsCorrectBudget() {
        Budget budget = budgetService.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH_STRING);

        assertThat(budget).isNotNull();
        assertThat(budget.getMonth()).isEqualTo(BudgetConstants.Default.YEAR_MONTH);
        assertThat(budget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_INCOME);
    }

    @Test
    void deleteBudget_ExistingBudget_DeletesSuccessfully() {
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElseThrow();

        budgetService.deleteBudget(budget.getId());

        assertThat(budgetRepository.findById(budget.getId())).isEmpty();
    }

    // -- Exception Tests --

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
