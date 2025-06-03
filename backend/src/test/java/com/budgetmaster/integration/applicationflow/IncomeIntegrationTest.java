package com.budgetmaster.integration.applicationflow;

import com.budgetmaster.application.controller.IncomeController;
import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.BudgetIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.IncomeIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.list.IncomeIntegrationListAssertions;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

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
public class IncomeIntegrationTest {

    @Autowired
    private IncomeController incomeController;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    private Income createdIncome;
    private IncomeRequest defaultIncomeRequest;

    @BeforeEach
    void setUp() {
        incomeRepository.deleteAll();
        budgetRepository.deleteAll();
        defaultIncomeRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();
        createdIncome = incomeController.createIncome(defaultIncomeRequest).getBody();
    }

    @Test
    void createIncome_ValidRequest_ProperlyPersistsAndSynchronizes() {
        IncomeIntegrationAssertions.assertIncome(createdIncome)
            .isDefaultIncome();
        
        Income persisted = incomeRepository.findById(createdIncome.getId()).orElse(null);
        IncomeIntegrationAssertions.assertIncome(persisted)
            .isEqualTo(createdIncome);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME);
    }

    @Test
    void updateIncome_ValidRequest_ProperlyUpdatesAndSynchronizes() {
        IncomeRequest updateRequest = IncomeRequestBuilder.updatedIncomeRequest().buildRequest();

        Income updatedIncome = incomeController.updateIncome(createdIncome.getId(), updateRequest).getBody();
        IncomeIntegrationAssertions.assertIncome(updatedIncome)
            .hasId(createdIncome.getId())
            .isUpdatedIncome();

        Income persisted = incomeRepository.findById(createdIncome.getId()).orElse(null);
        IncomeIntegrationAssertions.assertIncome(persisted)
            .isEqualTo(updatedIncome);

        Budget oldBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(oldBudget)
            .hasTotalIncome(BudgetConstants.ZeroValues.TOTAL_INCOME);

        Budget newBudget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(newBudget)
            .hasTotalIncome(BudgetConstants.Updated.TOTAL_INCOME);
    }

    @Test
    void deleteIncome_ValidId_ProperlyDeletesAndSynchronizes() {
        incomeController.deleteIncome(createdIncome.getId());
        IncomeIntegrationAssertions.assertIncomeDeleted(createdIncome, incomeRepository);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.ZeroValues.TOTAL_INCOME);
    }

    @Test
    void getAllIncomesForMonth_NoIncomes_ThrowsNotFoundException() {
        assertThatThrownBy(() -> incomeController.getAllIncomesForMonth(IncomeConstants.NonExistent.YEAR_MONTH_STRING))
            .isInstanceOf(IncomeNotFoundException.class)
            .hasMessageContaining(IncomeConstants.NonExistent.YEAR_MONTH_STRING);
    }

    @Test
    void getAllIncomesForMonth_WithIncomes_ReturnsCorrectList() {
        Income secondIncome = incomeController.createIncome(defaultIncomeRequest).getBody();
        List<Income> response = incomeController.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH_STRING).getBody();

        IncomeIntegrationListAssertions.assertIncomes(response)
            .hasSize(2)
            .contains(createdIncome, secondIncome);
    }

    @Test
    void getIncomeById_NonExistentId_ThrowsNotFoundException() {
        assertThatThrownBy(() -> incomeController.getIncomeById(IncomeConstants.NonExistent.ID))
            .isInstanceOf(IncomeNotFoundException.class)
            .hasMessageContaining(IncomeConstants.NonExistent.ID.toString());
    }
} 