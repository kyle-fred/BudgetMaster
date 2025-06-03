package com.budgetmaster.integration.service;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.application.service.IncomeService;
import com.budgetmaster.application.service.synchronization.IncomeBudgetSynchronizer;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.BudgetIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.IncomeIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.list.IncomeIntegrationListAssertions;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

import org.junit.jupiter.api.BeforeEach;
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
public class IncomeServiceIntegrationTest {

    @SuppressWarnings("removal")
    @SpyBean
    private IncomeBudgetSynchronizer incomeBudgetSynchronizer;

    @Autowired
    private IncomeService incomeService;
    
    @Autowired
    private IncomeRepository incomeRepository;
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    private IncomeRequest defaultRequest;
    
    @BeforeEach
    void setUp() {
        incomeRepository.deleteAll();
        budgetRepository.deleteAll();
        defaultRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();
    }
    
    @Test
    void createIncome_ValidRequest_ProperlyPersistsAndSynchronizes() {
        Income createdIncome = incomeService.createIncome(defaultRequest);
        
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
        Income createdIncome = incomeService.createIncome(defaultRequest);
        IncomeRequest updateRequest = IncomeRequestBuilder.updatedIncomeRequest().buildRequest();
        
        Income updatedIncome = incomeService.updateIncome(createdIncome.getId(), updateRequest);
        IncomeIntegrationAssertions.assertIncome(updatedIncome)
            .hasId(createdIncome.getId())
            .isUpdatedIncome();
        
        Income persisted = incomeRepository.findById(createdIncome.getId()).orElse(null);
        IncomeIntegrationAssertions.assertIncome(persisted)
            .isEqualTo(updatedIncome);
        
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.Updated.TOTAL_INCOME);
    }

    @Test
    void updateIncome_ChangesMonth_ProperlySynchronizesBothBudgets() {
        Income createdIncome = incomeService.createIncome(defaultRequest);
        IncomeRequest updateRequest = IncomeRequestBuilder.updatedIncomeRequest().buildRequest();
        incomeService.updateIncome(createdIncome.getId(), updateRequest);
        
        Budget oldBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(oldBudget)
            .hasTotalIncome(BudgetConstants.ZeroValues.TOTAL_INCOME);
        
        Budget newBudget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(newBudget)
            .hasTotalIncome(BudgetConstants.Updated.TOTAL_INCOME);
    }
    
    @Test
    void deleteIncome_ValidId_ProperlyDeletesAndSynchronizes() {
        Income createdIncome = incomeService.createIncome(defaultRequest);
        incomeService.deleteIncome(createdIncome.getId());
        IncomeIntegrationAssertions.assertIncomeDeleted(createdIncome, incomeRepository);
        
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.ZeroValues.TOTAL_INCOME);
    }
    
    // -- Transaction Rollback Tests --
    
    @Test
    void createIncome_SynchronizationFails_RollsBackTransaction() {
        Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
                .when(incomeBudgetSynchronizer).apply(any(Income.class));

        assertThatThrownBy(() -> incomeService.createIncome(defaultRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

        IncomeIntegrationListAssertions.assertIncomes(incomeRepository.findAll())
            .hasSize(0);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudgetNotInitialized(budget);
    }

    @Test
    void updateIncome_SynchronizationFails_RollsBackTransaction() {
        Income createdIncome = incomeService.createIncome(defaultRequest);
        IncomeRequest updatedRequest = IncomeRequestBuilder.updatedIncomeRequest().buildRequest();
        
        Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
            .when(incomeBudgetSynchronizer).reapply(any(Income.class), any(Income.class));

        assertThatThrownBy(() -> incomeService.updateIncome(createdIncome.getId(), updatedRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

        Income persisted = incomeRepository.findById(createdIncome.getId()).orElse(null);
        IncomeIntegrationAssertions.assertIncome(persisted)
            .isEqualTo(createdIncome);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME);
    }

    @Test
    void deleteIncome_SynchronizationFails_RollsBackTransaction() {
        Income createdIncome = incomeService.createIncome(defaultRequest);
        
        Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
            .when(incomeBudgetSynchronizer).retract(any(Income.class));

        assertThatThrownBy(() -> incomeService.deleteIncome(createdIncome.getId()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

        IncomeIntegrationAssertions.assertIncome(createdIncome)
            .isEqualTo(createdIncome);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        BudgetIntegrationAssertions.assertBudget(budget)
            .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME);
    }
    
    // -- Read and Exception Tests --
    
    @Test
    void getIncomeById_NonExistentId_ThrowsException() {
        assertThatThrownBy(() -> incomeService.getIncomeById(IncomeConstants.NonExistent.ID))
            .isInstanceOf(IncomeNotFoundException.class)
            .hasMessageContaining(IncomeConstants.NonExistent.ID.toString());
    }
    
    @Test
    void getAllIncomesForMonth_NoIncomes_ThrowsException() {
        assertThatThrownBy(() -> incomeService.getAllIncomesForMonth(IncomeConstants.NonExistent.YEAR_MONTH_STRING))
            .isInstanceOf(IncomeNotFoundException.class)
            .hasMessageContaining(IncomeConstants.NonExistent.YEAR_MONTH_STRING);
    }
}
