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
public class ExpenseIntegrationTest {

    @Autowired
    private ExpenseController expenseController;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    private Expense createdExpense;
    private ExpenseRequest defaultExpenseRequest;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        budgetRepository.deleteAll();
        defaultExpenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();
        createdExpense = expenseController.createExpense(defaultExpenseRequest).getBody();
    }

    @Test
    void createExpense_ValidRequest_ProperlyPersistsAndSynchronizes() {
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
    void updateExpense_ValidRequest_ProperlyUpdatesAndSynchronizes() {
        ExpenseRequest updateRequest = ExpenseRequestBuilder.updatedExpenseRequest().buildRequest();

        Expense updatedExpense = expenseController.updateExpense(createdExpense.getId(), updateRequest).getBody();
        ExpenseIntegrationAssertions.assertExpense(updatedExpense)
            .hasId(createdExpense.getId())
            .isUpdatedExpense();

        Expense persisted = expenseRepository.findById(createdExpense.getId()).orElse(null);
        ExpenseIntegrationAssertions.assertExpense(persisted)
            .isEqualTo(updatedExpense);

        Budget oldBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(oldBudget)
            .hasTotalExpense(BudgetConstants.ZeroValues.TOTAL_EXPENSE);

        Budget newBudget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(newBudget)
            .hasTotalExpense(BudgetConstants.Updated.TOTAL_EXPENSE);
    }

    @Test
    void deleteExpense_ValidId_ProperlyDeletesAndSynchronizes() {
        expenseController.deleteExpense(createdExpense.getId());
        ExpenseIntegrationAssertions.assertExpenseDeleted(createdExpense, expenseRepository);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(budget)
            .hasTotalExpense(BudgetConstants.ZeroValues.TOTAL_EXPENSE);
    }

    @Test
    void getAllExpensesForMonth_NoExpenses_ThrowsNotFoundException() {
        assertThatThrownBy(() -> expenseController.getAllExpensesForMonth(ExpenseConstants.NonExistent.YEAR_MONTH_STRING))
            .isInstanceOf(ExpenseNotFoundException.class)
            .hasMessageContaining(ExpenseConstants.NonExistent.YEAR_MONTH_STRING);
    }

    @Test
    void getAllExpensesForMonth_WithExpenses_ReturnsCorrectList() {
        Expense secondExpense = expenseController.createExpense(defaultExpenseRequest).getBody();
        List<Expense> response = expenseController.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH_STRING).getBody();

        ExpenseIntegrationListAssertions.assertExpenses(response)
            .hasSize(2)
            .contains(createdExpense, secondExpense);
    }

    @Test
    void getExpenseById_NonExistentId_ThrowsNotFoundException() {
        assertThatThrownBy(() -> expenseController.getExpenseById(ExpenseConstants.NonExistent.ID))
            .isInstanceOf(ExpenseNotFoundException.class)
            .hasMessageContaining(ExpenseConstants.NonExistent.ID.toString());
    }
} 