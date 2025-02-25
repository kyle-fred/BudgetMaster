package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.model.Budget;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BudgetServiceTest {
	
	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);
	
    @Test
    void shouldSaveBudgetSuccessfully() {
        Budget expectedBudget = new Budget();
        expectedBudget.setId(1L);
        expectedBudget.setIncome(3000.0);
        expectedBudget.setExpenses(1500.0);
        expectedBudget.setSavings(1500.0);
    	
        Mockito.when(budgetRepository.save(Mockito.any(Budget.class)))
        	.thenReturn(expectedBudget);
        
        BudgetRequest budgetRequest = new BudgetRequest();
        budgetRequest.setIncome(3000.0);
        budgetRequest.setExpenses(1500.0);

        Budget savedBudget = budgetService.createBudget(budgetRequest);

        assertNotNull(savedBudget);
        assertEquals(1L, savedBudget.getId());
        assertEquals(3000.0, savedBudget.getIncome());
        assertEquals(1500.0, savedBudget.getExpenses());
        assertEquals(1500.0, savedBudget.getSavings());

        Mockito.verify(budgetRepository, Mockito.times(1))
        	.save(Mockito.any(Budget.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaveFails() {
    	
        Mockito.when(budgetRepository.save(Mockito.any(Budget.class)))
               .thenThrow(new DataIntegrityViolationException("Duplicate Entry"));

        BudgetRequest budgetRequest = new BudgetRequest();
        budgetRequest.setIncome(3000.0);
        budgetRequest.setExpenses(1500.0);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> budgetService.createBudget(budgetRequest));
    }
}