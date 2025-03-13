package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.model.Budget;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Optional;

class BudgetServiceTest {
	
	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);
	
    @Test
    void shouldCreateAndSaveBudgetSuccessfully() {
        
    	Budget expectedBudget = new Budget();
        expectedBudget.setId(1L);
        expectedBudget.setIncome(3000.0);
        expectedBudget.setExpenses(1500.0);
        expectedBudget.setSavings(1500.0);
    	
        // Mock successful creation
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
    void shouldReturnBudgetWhenExists() {
    	Budget budget = new Budget(3000.0, 1500.0);
    	budget.setId(1L);
    	
    	// Mock successful read
    	Mockito.when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));
    	
    	Optional<Budget> retrievedBudget = budgetService.getBudgetById(1L);
    	
    	assertTrue(retrievedBudget.isPresent());
    	assertEquals(1L, retrievedBudget.get().getId());
    	assertEquals(3000.0, retrievedBudget.get().getIncome());
    	assertEquals(1500.0, retrievedBudget.get().getExpenses());
    	assertEquals(1500.0, retrievedBudget.get().getSavings());
    }
    
    @Test
    void shouldUpdateBudgetWhenExists() {
        Budget existingBudget = new Budget(3000.0, 1500.0);
        existingBudget.setId(1L);

        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setIncome(6000.0);
        updateRequest.setExpenses(3000.0);

        // Mock successful update
        Mockito.when(budgetRepository.findById(1L)).thenReturn(Optional.of(existingBudget));
        Mockito.when(budgetRepository.save(Mockito.any(Budget.class))).thenReturn(existingBudget);

        Optional<Budget> updatedBudget = budgetService.updateBudget(1L, updateRequest);

        assertTrue(updatedBudget.isPresent());
        assertEquals(6000.0, updatedBudget.get().getIncome());
        assertEquals(3000.0, updatedBudget.get().getExpenses());
        assertEquals(3000.0, updatedBudget.get().getSavings());
        
        Mockito.verify(budgetRepository, Mockito.times(1)).save(existingBudget);
    }
    
    @Test
    void shouldDeleteBudgetWhenExists() {
    	// Mock successful delete
    	Mockito.when(budgetRepository.existsById(1L)).thenReturn(true);
    	Mockito.doNothing().when(budgetRepository).deleteById(1L);

        boolean deleted = budgetService.deleteBudget(1L);

        assertTrue(deleted);
        Mockito.verify(budgetRepository, Mockito.times(1)).deleteById(1L);
    }
    
    @Test
    void shouldThrowExceptionWhenSaveFails() {
    	// Mock unsuccessful creation
        Mockito.when(budgetRepository.save(Mockito.any(Budget.class)))
               .thenThrow(new DataIntegrityViolationException("Duplicate Entry"));

        BudgetRequest budgetRequest = new BudgetRequest();
        budgetRequest.setIncome(3000.0);
        budgetRequest.setExpenses(1500.0);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> budgetService.createBudget(budgetRequest));
    }
    
    @Test
    void shouldReturnEmptyWhenBudgetDoesNotExist() {
    	// Mock unsuccessful read
    	Mockito.when(budgetRepository.findById(99L)).thenReturn(Optional.empty());
        
        Optional<Budget> retrievedBudget = budgetService.getBudgetById(99L);
        
        assertFalse(retrievedBudget.isPresent());
    }
    
    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentBudget() {
        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setIncome(6000.0);
        updateRequest.setExpenses(3000.0);

        // Mock unsuccessful update
        Mockito.when(budgetRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Budget> updatedBudget = budgetService.updateBudget(99L, updateRequest);

        assertFalse(updatedBudget.isPresent());
        Mockito.verify(budgetRepository, Mockito.never()).save(Mockito.any(Budget.class));
    }
    
    @Test
    void shouldReturnFalseWhenDeletingNonExistentBudget() {
    	// Mock unsuccessful delete
    	Mockito.when(budgetRepository.existsById(99L)).thenReturn(false);

        boolean deleted = budgetService.deleteBudget(99L);

        assertFalse(deleted);
        Mockito.verify(budgetRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}