package com.budgetmaster.income.service;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.application.service.IncomeService;
import com.budgetmaster.application.service.synchronization.IncomeBudgetSynchronizer;
import com.budgetmaster.config.TestContainersConfig;
import com.budgetmaster.exception.IncomeNotFoundException;
import com.budgetmaster.exception.codes.ErrorCode;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.constants.Fields;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
import com.budgetmaster.testsupport.income.factory.IncomeFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@Testcontainers
@SpringBootTest
@Import(TestContainersConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class IncomeServiceIntegrationTest {
    // -- Dependencies --
    @SpyBean
    private IncomeBudgetSynchronizer incomeBudgetSynchronizer;

    @Autowired
    private IncomeService incomeService;
    
    @Autowired
    private IncomeRepository incomeRepository;
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    // -- Test Objects --
    private IncomeRequest defaultRequest;
    
    @BeforeEach
    void setUp() {
        defaultRequest = IncomeFactory.createDefaultIncomeRequest();
        incomeRepository.deleteAll();
        budgetRepository.deleteAll();
    }
    
    // -- Happy Path Tests --
    
    @Test
    void createIncome_ValidRequest_ProperlyPersistsAndSynchronizes() {
        Income result = incomeService.createIncome(defaultRequest);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(IncomeConstants.Default.NAME);
        assertThat(result.getSource()).isEqualTo(IncomeConstants.Default.SOURCE);
        assertThat(result.getMoney().getAmount()).isEqualByComparingTo(IncomeConstants.Default.AMOUNT);
        assertThat(result.getMoney().getCurrency()).isEqualTo(IncomeConstants.Default.CURRENCY);
        assertThat(result.getType()).isEqualTo(IncomeConstants.Default.TYPE);
        assertThat(result.getMonth()).isEqualTo(IncomeConstants.Default.YEAR_MONTH);
        
        Income persisted = incomeRepository.findById(result.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(Fields.Audit.CREATED_AT, Fields.Audit.LAST_UPDATED_AT)
            .isEqualTo(result);
        
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_INCOME);
    }
    
    @Test
    void updateIncome_ValidRequest_ProperlyUpdatesAndSynchronizes() {
        Income original = incomeService.createIncome(defaultRequest);
        IncomeRequest updateRequest = IncomeFactory.createUpdatedIncomeRequest();
        
        Income result = incomeService.updateIncome(original.getId(), updateRequest);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(IncomeConstants.Updated.NAME);
        assertThat(result.getSource()).isEqualTo(IncomeConstants.Updated.SOURCE);
        assertThat(result.getMoney().getAmount()).isEqualByComparingTo(IncomeConstants.Updated.AMOUNT);
        assertThat(result.getMoney().getCurrency()).isEqualTo(IncomeConstants.Default.CURRENCY);
        assertThat(result.getType()).isEqualTo(IncomeConstants.Updated.TYPE);
        assertThat(result.getMonth()).isEqualTo(IncomeConstants.Updated.YEAR_MONTH);
        
        Income persisted = incomeRepository.findById(result.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(Fields.Audit.CREATED_AT, Fields.Audit.LAST_UPDATED_AT)
            .isEqualTo(result);
        
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.Updated.TOTAL_INCOME);
    }

    @Test
    void updateIncome_ChangesMonth_ProperlySynchronizesBothBudgets() {
        Income income = incomeService.createIncome(defaultRequest);
        IncomeRequest updateRequest = IncomeFactory.createUpdatedIncomeRequest();
        
        incomeService.updateIncome(income.getId(), updateRequest);
        
        Budget oldBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(oldBudget).isNotNull();
        assertThat(oldBudget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.ZeroValues.TOTAL_INCOME);
        
        Budget newBudget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        assertThat(newBudget).isNotNull();
        assertThat(newBudget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.Updated.TOTAL_INCOME);
    }
    
    @Test
    void deleteIncome_ValidId_ProperlyDeletesAndSynchronizes() {
        Income income = incomeService.createIncome(defaultRequest);
        
        incomeService.deleteIncome(income.getId());
        
        assertThat(incomeRepository.findById(income.getId())).isEmpty();
        
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.ZeroValues.TOTAL_INCOME);
    }
    
    // -- Transaction Rollback Tests --
    
    @Test
    void createIncome_SynchronizationFails_RollsBackTransaction() {
        Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
                .when(incomeBudgetSynchronizer).apply(any(Income.class));

        assertThatThrownBy(() -> incomeService.createIncome(defaultRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

        assertThat(incomeRepository.findAll()).isEmpty();
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNull();
    }

    @Test
    void updateIncome_SynchronizationFails_RollsBackTransaction() {
        Income original = incomeService.createIncome(defaultRequest);
        IncomeRequest updatedRequest = IncomeFactory.createUpdatedIncomeRequest();
        
        Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
            .when(incomeBudgetSynchronizer).reapply(any(Income.class), any(Income.class));

        assertThatThrownBy(() -> incomeService.updateIncome(original.getId(), updatedRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

        Income persisted = incomeRepository.findById(original.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(Fields.Audit.CREATED_AT, Fields.Audit.LAST_UPDATED_AT)
            .isEqualTo(original);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_INCOME);
    }

    @Test
    void deleteIncome_SynchronizationFails_RollsBackTransaction() {
        Income income = incomeService.createIncome(defaultRequest);
        
        Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
            .when(incomeBudgetSynchronizer).retract(any(Income.class));

        assertThatThrownBy(() -> incomeService.deleteIncome(income.getId()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

        assertThat(incomeRepository.findById(income.getId())).isPresent();
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalIncome()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_INCOME);
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
