package com.budgetmaster.expense.service;

import com.budgetmaster.application.service.ExpenseService;
import com.budgetmaster.application.service.synchronization.ExpenseBudgetSynchronizer;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.common.enums.ErrorCode;
import com.budgetmaster.config.TestContainersConfig;
import com.budgetmaster.exception.ExpenseNotFoundException;
import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.expense.repository.ExpenseRepository;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.constants.Fields;
import com.budgetmaster.testsupport.expense.constants.ExpenseConstants;
import com.budgetmaster.testsupport.expense.factory.ExpenseFactory;

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
public class ExpenseServiceIntegrationTest {
    // -- Dependencies --
    @SpyBean
    private ExpenseBudgetSynchronizer expenseBudgetSynchronizer;

    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    // -- Test Objects --
    private ExpenseRequest defaultRequest;
    
    @BeforeEach
    void setUp() {
        defaultRequest = ExpenseFactory.createDefaultExpenseRequest();
        expenseRepository.deleteAll();
        budgetRepository.deleteAll();
    }
    
    // -- Happy Path Tests --
    
    @Test
    void createExpense_ValidRequest_ProperlyPersistsAndSynchronizes() {
        Expense result = expenseService.createExpense(defaultRequest);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(ExpenseConstants.Default.NAME);
        assertThat(result.getCategory()).isEqualTo(ExpenseConstants.Default.CATEGORY);
        assertThat(result.getMoney().getAmount()).isEqualByComparingTo(ExpenseConstants.Default.AMOUNT);
        assertThat(result.getMoney().getCurrency()).isEqualTo(ExpenseConstants.Default.CURRENCY);
        assertThat(result.getType()).isEqualTo(ExpenseConstants.Default.TYPE);
        assertThat(result.getMonth()).isEqualTo(ExpenseConstants.Default.YEAR_MONTH);
        
        Expense persisted = expenseRepository.findById(result.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(Fields.Audit.CREATED_AT, Fields.Audit.LAST_UPDATED_AT)
            .isEqualTo(result);
        
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_EXPENSE);
    }
    
    @Test
    void updateExpense_ValidRequest_ProperlyUpdatesAndSynchronizes() {
        Expense original = expenseService.createExpense(defaultRequest);
        ExpenseRequest updateRequest = ExpenseFactory.createUpdatedExpenseRequest();
        
        Expense result = expenseService.updateExpense(original.getId(), updateRequest);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(ExpenseConstants.Updated.NAME);
        assertThat(result.getCategory()).isEqualTo(ExpenseConstants.Updated.CATEGORY);
        assertThat(result.getMoney().getAmount()).isEqualByComparingTo(ExpenseConstants.Updated.AMOUNT);
        assertThat(result.getMoney().getCurrency()).isEqualTo(ExpenseConstants.Default.CURRENCY);
        assertThat(result.getType()).isEqualTo(ExpenseConstants.Updated.TYPE);
        assertThat(result.getMonth()).isEqualTo(ExpenseConstants.Updated.YEAR_MONTH);
        
        Expense persisted = expenseRepository.findById(result.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(Fields.Audit.CREATED_AT, Fields.Audit.LAST_UPDATED_AT)
            .isEqualTo(result);
        
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.Updated.TOTAL_EXPENSE);
    }

    @Test
    void updateExpense_ChangesMonth_ProperlySynchronizesBothBudgets() {
        Expense expense = expenseService.createExpense(defaultRequest);
        ExpenseRequest updateRequest = ExpenseFactory.createUpdatedExpenseRequest();
        
        expenseService.updateExpense(expense.getId(), updateRequest);
        
        Budget oldBudget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(oldBudget).isNotNull();
        assertThat(oldBudget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.ZeroValues.TOTAL_EXPENSE);
        
        Budget newBudget = budgetRepository.findByMonth(BudgetConstants.Updated.YEAR_MONTH).orElse(null);
        assertThat(newBudget).isNotNull();
        assertThat(newBudget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.Updated.TOTAL_EXPENSE);
    }
    
    @Test
    void deleteExpense_ValidId_ProperlyDeletesAndSynchronizes() {
        Expense expense = expenseService.createExpense(defaultRequest);
        
        expenseService.deleteExpense(expense.getId());
        
        assertThat(expenseRepository.findById(expense.getId())).isEmpty();
        
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.ZeroValues.TOTAL_EXPENSE);
    }
    
    // -- Transaction Rollback Tests --
    
    @Test
    void createExpense_SynchronizationFails_RollsBackTransaction() {
        Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
                .when(expenseBudgetSynchronizer).apply(any(Expense.class));

        assertThatThrownBy(() -> expenseService.createExpense(defaultRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

        assertThat(expenseRepository.findAll()).isEmpty();
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNull();
    }

    @Test
    void updateExpense_SynchronizationFails_RollsBackTransaction() {
        Expense original = expenseService.createExpense(defaultRequest);
        ExpenseRequest updatedRequest = ExpenseFactory.createUpdatedExpenseRequest();
        
        Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
            .when(expenseBudgetSynchronizer).reapply(any(Expense.class), any(Expense.class));

        assertThatThrownBy(() -> expenseService.updateExpense(original.getId(), updatedRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

        Expense persisted = expenseRepository.findById(original.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted)
            .usingRecursiveComparison()
            .ignoringFields(Fields.Audit.CREATED_AT, Fields.Audit.LAST_UPDATED_AT)
            .isEqualTo(original);

        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_EXPENSE);
    }

    @Test
    void deleteExpense_SynchronizationFails_RollsBackTransaction() {
        Expense expense = expenseService.createExpense(defaultRequest);
        
        Mockito.doThrow(new RuntimeException(ErrorCode.SYNCHRONIZATION_FAILED.getMessage()))
            .when(expenseBudgetSynchronizer).retract(any(Expense.class));

        assertThatThrownBy(() -> expenseService.deleteExpense(expense.getId()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(ErrorCode.SYNCHRONIZATION_FAILED.getMessage());

        assertThat(expenseRepository.findById(expense.getId())).isPresent();
        Budget budget = budgetRepository.findByMonth(BudgetConstants.Default.YEAR_MONTH).orElse(null);
        assertThat(budget).isNotNull();
        assertThat(budget.getTotalExpense()).isEqualByComparingTo(BudgetConstants.Default.TOTAL_EXPENSE);
    }
    
    // -- Read and Exception Tests --
    
    @Test
    void getExpenseById_NonExistentId_ThrowsException() {
        assertThatThrownBy(() -> expenseService.getExpenseById(ExpenseConstants.NonExistent.ID))
            .isInstanceOf(ExpenseNotFoundException.class)
            .hasMessageContaining(ExpenseConstants.NonExistent.ID.toString());
    }
    
    @Test
    void getAllExpensesForMonth_NoExpenses_ThrowsException() {
        assertThatThrownBy(() -> expenseService.getAllExpensesForMonth(ExpenseConstants.NonExistent.YEAR_MONTH_STRING))
            .isInstanceOf(ExpenseNotFoundException.class)
            .hasMessageContaining(ExpenseConstants.NonExistent.YEAR_MONTH_STRING);
    }
}
