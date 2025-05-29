package com.budgetmaster.income.integration;

import com.budgetmaster.application.controller.IncomeController;
import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.config.TestContainersConfig;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.constants.Fields;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
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

import java.util.List;

@Testcontainers
@SpringBootTest
@Import(TestContainersConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class IncomeIntegrationTest {
    // -- Dependencies --
    @Autowired
    private IncomeController incomeController;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    // -- Test Objects --
    private IncomeRequest defaultIncomeRequest;

    @BeforeEach
    void setUp() {
        defaultIncomeRequest = IncomeFactory.createDefaultIncomeRequest();
        incomeRepository.deleteAll();
        budgetRepository.deleteAll();
    }

    @Test
    void createIncome_ValidRequest_ProperlyPersistsAndSynchronizes() {
        Income response = incomeController.createIncome(defaultIncomeRequest).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo(IncomeConstants.Default.NAME);
        assertThat(response.getSource()).isEqualTo(IncomeConstants.Default.SOURCE);
        assertThat(response.getMoney().getAmount()).isEqualByComparingTo(IncomeConstants.Default.AMOUNT);
        assertThat(response.getMoney().getCurrency()).isEqualTo(IncomeConstants.Default.CURRENCY);
        assertThat(response.getType()).isEqualTo(IncomeConstants.Default.TYPE);
        assertThat(response.getMonth()).isEqualTo(IncomeConstants.Default.YEAR_MONTH);

        Income persisted = incomeRepository.findById(response.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(Fields.Audit.CREATED_AT, Fields.Audit.LAST_UPDATED_AT)
            .isEqualTo(response);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_INCOME);
    }

    @Test
    void updateIncome_ValidRequest_ProperlyUpdatesAndSynchronizes() {
        Income original = incomeController.createIncome(defaultIncomeRequest).getBody();
        IncomeRequest updateRequest = IncomeFactory.createUpdatedIncomeRequest();

        Income response = incomeController.updateIncome(original.getId(), updateRequest).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(original.getId());
        assertThat(response.getName()).isEqualTo(IncomeConstants.Updated.NAME);
        assertThat(response.getSource()).isEqualTo(IncomeConstants.Updated.SOURCE);
        assertThat(response.getMoney().getAmount()).isEqualByComparingTo(IncomeConstants.Updated.AMOUNT);
        assertThat(response.getMoney().getCurrency()).isEqualTo(IncomeConstants.Default.CURRENCY);
        assertThat(response.getType()).isEqualTo(IncomeConstants.Updated.TYPE);
        assertThat(response.getMonth()).isEqualTo(IncomeConstants.Updated.YEAR_MONTH);

        Income persisted = incomeRepository.findById(response.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(Fields.Audit.CREATED_AT, Fields.Audit.LAST_UPDATED_AT)
            .isEqualTo(response);

        Budget oldBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(oldBudget).isNotNull();
        assertThat(oldBudget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.ZeroValues.TOTAL_INCOME);

        Budget newBudget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        assertThat(newBudget).isNotNull();
        assertThat(newBudget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.Updated.TOTAL_INCOME);
    }

    @Test
    void deleteIncome_ValidId_ProperlyDeletesAndSynchronizes() {
        Income income = incomeController.createIncome(defaultIncomeRequest).getBody();
        incomeController.deleteIncome(income.getId());

        assertThat(incomeRepository.findById(income.getId())).isEmpty();

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.ZeroValues.TOTAL_INCOME);
    }

    @Test
    void getAllIncomesForMonth_NoIncomes_ThrowsNotFoundException() {
        assertThatThrownBy(() -> incomeController.getAllIncomesForMonth(IncomeConstants.NonExistent.YEAR_MONTH_STRING))
            .isInstanceOf(IncomeNotFoundException.class)
            .hasMessageContaining(IncomeConstants.NonExistent.YEAR_MONTH_STRING);
    }

    @Test
    void getAllIncomesForMonth_WithIncomes_ReturnsCorrectList() {
        Income income = incomeController.createIncome(defaultIncomeRequest).getBody();
        List<Income> response = incomeController.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH_STRING).getBody();

        assertThat(response).hasSize(1);
        assertThat(response.get(0))
            .usingRecursiveComparison()
            .ignoringFields(Fields.Audit.CREATED_AT, Fields.Audit.LAST_UPDATED_AT)
            .isEqualTo(income);
    }

    @Test
    void getIncomeById_NonExistentId_ThrowsNotFoundException() {
        assertThatThrownBy(() -> incomeController.getIncomeById(IncomeConstants.NonExistent.ID))
            .isInstanceOf(IncomeNotFoundException.class)
            .hasMessageContaining(IncomeConstants.NonExistent.ID.toString());
    }
} 