package com.budgetmaster.integration.applicationflow;

import com.budgetmaster.application.controller.BudgetController;
import com.budgetmaster.application.controller.ExpenseController;
import com.budgetmaster.application.controller.IncomeController;
import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.builder.ExpenseFactory;
import com.budgetmaster.testsupport.builder.IncomeFactory;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

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
public class BudgetIntegrationTest {
    // -- Dependencies --
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
        incomeController.createIncome(IncomeFactory.createDefaultIncomeRequest());
        expenseController.createExpense(ExpenseFactory.createDefaultExpenseRequest());
    }

    @Test
    void getBudgetByMonth_FoundBudget_ReturnsBudget() {
        Budget budget = budgetController.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH_STRING).getBody();

        assertThat(budget).isNotNull();
        assertThat(budget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_INCOME);
        assertThat(budget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_EXPENSE);
        assertThat(budget.getSavings()).isEqualByComparingTo(BudgetConstants.Default.SAVINGS);
    }

    @Test
    void getBudgetByMonth_NoBudget_ThrowsNotFoundException() {
        assertThatThrownBy(() -> budgetController.getBudgetByMonth(BudgetConstants.NonExistent.YEAR_MONTH_STRING))
            .isInstanceOf(BudgetNotFoundException.class)
            .hasMessageContaining(BudgetConstants.NonExistent.YEAR_MONTH_STRING);
    }

    @Test
    void deleteBudget_ValidId_DeletesBudget() {
        Budget budget = budgetController.getBudgetByMonth(BudgetConstants.Default.YEAR_MONTH_STRING).getBody();
        budgetController.deleteBudget(budget.getId());

        assertThat(budgetRepository.findById(budget.getId())).isEmpty();
    }

    @Test
    void deleteBudget_NonExistentId_ThrowsNotFoundException() {
        assertThatThrownBy(() -> budgetController.deleteBudget(BudgetConstants.NonExistent.ID))
            .isInstanceOf(BudgetNotFoundException.class)
            .hasMessageContaining(BudgetConstants.NonExistent.ID.toString());
    }
} 