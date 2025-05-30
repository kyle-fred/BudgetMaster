package com.budgetmaster.integration.applicationflow;

import com.budgetmaster.application.controller.ExpenseController;
import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.application.exception.ExpenseNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.repository.ExpenseRepository;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.builder.ExpenseFactory;
import com.budgetmaster.testsupport.constants.FieldConstants;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

@Testcontainers
@SpringBootTest
@Import(TestContainersConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ExpenseIntegrationTest {
    // -- Dependencies --
    @Autowired
    private ExpenseController expenseController;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    // -- Test Objects --
    private ExpenseRequest defaultExpenseRequest;

    @BeforeEach
    void setUp() {
        defaultExpenseRequest = ExpenseFactory.createDefaultExpenseRequest();
        expenseRepository.deleteAll();
        budgetRepository.deleteAll();
    }

    @Test
    void createExpense_ValidRequest_ProperlyPersistsAndSynchronizes() {
        Expense response = expenseController.createExpense(defaultExpenseRequest).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo(ExpenseConstants.Default.NAME);
        assertThat(response.getCategory()).isEqualTo(ExpenseConstants.Default.CATEGORY);
        assertThat(response.getMoney().getAmount()).isEqualByComparingTo(ExpenseConstants.Default.AMOUNT);
        assertThat(response.getMoney().getCurrency()).isEqualTo(ExpenseConstants.Default.CURRENCY);
        assertThat(response.getType()).isEqualTo(ExpenseConstants.Default.TYPE);
        assertThat(response.getMonth()).isEqualTo(ExpenseConstants.Default.YEAR_MONTH);

        Expense persisted = expenseRepository.findById(response.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(FieldConstants.Audit.CREATED_AT, FieldConstants.Audit.LAST_UPDATED_AT)
            .isEqualTo(response);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_EXPENSE);
    }

    @Test
    void updateExpense_ValidRequest_ProperlyUpdatesAndSynchronizes() {
        Expense original = expenseController.createExpense(defaultExpenseRequest).getBody();
        ExpenseRequest updateRequest = ExpenseFactory.createUpdatedExpenseRequest();

        Expense response = expenseController.updateExpense(original.getId(), updateRequest).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(original.getId());
        assertThat(response.getName()).isEqualTo(ExpenseConstants.Updated.NAME);
        assertThat(response.getCategory()).isEqualTo(ExpenseConstants.Updated.CATEGORY);
        assertThat(response.getMoney().getAmount()).isEqualByComparingTo(ExpenseConstants.Updated.AMOUNT);
        assertThat(response.getMoney().getCurrency()).isEqualTo(ExpenseConstants.Default.CURRENCY);
        assertThat(response.getType()).isEqualTo(ExpenseConstants.Updated.TYPE);
        assertThat(response.getMonth()).isEqualTo(ExpenseConstants.Updated.YEAR_MONTH);

        Expense persisted = expenseRepository.findById(response.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(FieldConstants.Audit.CREATED_AT, FieldConstants.Audit.LAST_UPDATED_AT)
            .isEqualTo(response);

        Budget oldBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(oldBudget).isNotNull();
        assertThat(oldBudget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.ZeroValues.TOTAL_EXPENSE);

        Budget newBudget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        assertThat(newBudget).isNotNull();
        assertThat(newBudget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.Updated.TOTAL_EXPENSE);
    }

    @Test
    void deleteExpense_ValidId_ProperlyDeletesAndSynchronizes() {
        Expense expense = expenseController.createExpense(defaultExpenseRequest).getBody();
        expenseController.deleteExpense(expense.getId());

        assertThat(expenseRepository.findById(expense.getId())).isEmpty();

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.ZeroValues.TOTAL_EXPENSE);
    }

    @Test
    void getAllExpensesForMonth_NoExpenses_ThrowsNotFoundException() {
        assertThatThrownBy(() -> expenseController.getAllExpensesForMonth(ExpenseConstants.NonExistent.YEAR_MONTH_STRING))
            .isInstanceOf(ExpenseNotFoundException.class)
            .hasMessageContaining(ExpenseConstants.NonExistent.YEAR_MONTH_STRING);
    }

    @Test
    void getAllExpensesForMonth_WithExpenses_ReturnsCorrectList() {
        Expense expense = expenseController.createExpense(defaultExpenseRequest).getBody();
        List<Expense> response = expenseController.getAllExpensesForMonth(ExpenseConstants.Default.YEAR_MONTH_STRING).getBody();

        assertThat(response).hasSize(1);
        assertThat(response.get(0))
            .usingRecursiveComparison()
            .ignoringFields(FieldConstants.Audit.CREATED_AT, FieldConstants.Audit.LAST_UPDATED_AT)
            .isEqualTo(expense);
    }

    @Test
    void getExpenseById_NonExistentId_ThrowsNotFoundException() {
        assertThatThrownBy(() -> expenseController.getExpenseById(ExpenseConstants.NonExistent.ID))
            .isInstanceOf(ExpenseNotFoundException.class)
            .hasMessageContaining(ExpenseConstants.NonExistent.ID.toString());
    }
} 