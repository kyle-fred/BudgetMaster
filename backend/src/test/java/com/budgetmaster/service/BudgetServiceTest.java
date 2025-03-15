package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.model.Budget;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

class BudgetServiceTest {
	
	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);
	
	@Test
	void shouldCreateAndSaveBudgetSuccessfully() {
	    
	    YearMonth expectedMonthYear = YearMonth.of(2025, 3);
	    LocalDateTime now = LocalDateTime.now().withNano(0);

	    Budget expectedBudget = new Budget();
	    expectedBudget.setId(1L);
	    expectedBudget.setIncome(3000.0);
	    expectedBudget.setExpenses(1500.0);
	    expectedBudget.setSavings(1500.0);
	    expectedBudget.setMonthYear(expectedMonthYear);
	    expectedBudget.setCreatedAt(now);
	    expectedBudget.setLastUpdatedAt(now);
	    
	    // Mock successful creation
	    Mockito.when(budgetRepository.save(Mockito.any(Budget.class)))
	        .thenReturn(expectedBudget);
	    
	    BudgetRequest budgetRequest = new BudgetRequest();
	    budgetRequest.setIncome(3000.0);
	    budgetRequest.setExpenses(1500.0);
	    budgetRequest.setMonthYear(expectedMonthYear.toString());

	    Budget savedBudget = budgetService.createBudget(budgetRequest);

	    assertNotNull(savedBudget);
	    assertEquals(1L, savedBudget.getId());
	    assertEquals(3000.0, savedBudget.getIncome());
	    assertEquals(1500.0, savedBudget.getExpenses());
	    assertEquals(1500.0, savedBudget.getSavings());
	    assertEquals(expectedMonthYear, savedBudget.getMonthYear());
	    assertNotNull(savedBudget.getCreatedAt());
	    assertNotNull(savedBudget.getLastUpdatedAt());

	    Mockito.verify(budgetRepository, Mockito.times(1))
	        .save(Mockito.any(Budget.class));
	}
	
	@Test 
	void shouldSetCreatedAtOnSave() {
		YearMonth expectedMonthYear = YearMonth.of(2025, 3);
		LocalDateTime now = LocalDateTime.now().withNano(0);
		
		BudgetRequest budgetRequest = new BudgetRequest();
	    budgetRequest.setIncome(3000.0);
	    budgetRequest.setExpenses(1500.0);
	    budgetRequest.setMonthYear(expectedMonthYear.toString());
	    
	    Budget expectedBudget = new Budget(3000.0, 1500.0, expectedMonthYear);
	    expectedBudget.setId(1L);
	    expectedBudget.setCreatedAt(now);
	    expectedBudget.setLastUpdatedAt(now);
	    
	    // Mock successful creation
	    Mockito.when(budgetRepository.save(Mockito.any(Budget.class)))
	        .thenReturn(expectedBudget);
	    
	    Budget savedBudget = budgetService.createBudget(budgetRequest);
	    
	    assertNotNull(savedBudget.getCreatedAt());
	    assertNotNull(savedBudget.getLastUpdatedAt());
	}
    
	@Test
	void shouldReturnBudgetWhenExists() {

	    YearMonth testYearMonth = YearMonth.of(2023, 5);
	    LocalDateTime now = LocalDateTime.now().withNano(0);
	    
	    Budget budget = new Budget(3000.0, 1500.0, testYearMonth);
	    budget.setId(1L);
	    budget.setCreatedAt(now);
	    budget.setLastUpdatedAt(now);

	    // Mock successful read
	    Mockito.when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

	    Optional<Budget> retrievedBudget = budgetService.getBudgetById(1L);

	    assertTrue(retrievedBudget.isPresent());
	    assertEquals(1L, retrievedBudget.get().getId());
	    assertEquals(3000.0, retrievedBudget.get().getIncome());
	    assertEquals(1500.0, retrievedBudget.get().getExpenses());
	    assertEquals(1500.0, retrievedBudget.get().getSavings());
	    assertEquals(testYearMonth, retrievedBudget.get().getMonthYear());
	    assertNotNull(retrievedBudget.get().getCreatedAt());
	    assertNotNull(retrievedBudget.get().getLastUpdatedAt());
	}

    
    @Test
    void shouldUpdateBudgetWhenExists() {

        YearMonth testYearMonth = YearMonth.of(2023, 5);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        Budget existingBudget = new Budget(3000.0, 1500.0, testYearMonth);
        existingBudget.setId(1L);
        existingBudget.setCreatedAt(now);
        existingBudget.setLastUpdatedAt(now);

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
        assertEquals(testYearMonth, updatedBudget.get().getMonthYear());
        assertNotNull(updatedBudget.get().getCreatedAt());
        assertNotNull(updatedBudget.get().getLastUpdatedAt());

        Mockito.verify(budgetRepository, Mockito.times(1)).save(existingBudget);
    }
    
    @Test
    void shouldUpdateLastUpdatedAtOnModification() {
        YearMonth testYearMonth = YearMonth.of(2025, 5);
        LocalDateTime createdAt = LocalDateTime.now().withNano(0).minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now().withNano(0);

        Budget existingBudget = new Budget(3000.0, 1500.0, testYearMonth);
        existingBudget.setId(1L);
        existingBudget.setCreatedAt(createdAt);
        existingBudget.setLastUpdatedAt(createdAt);

        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setIncome(5000.0);
        updateRequest.setExpenses(2500.0);

        Budget updatedBudget = new Budget(5000.0, 2500.0, testYearMonth);
        updatedBudget.setId(1L);
        updatedBudget.setCreatedAt(createdAt);
        updatedBudget.setLastUpdatedAt(updatedAt);

        Mockito.when(budgetRepository.findById(1L)).thenReturn(Optional.of(existingBudget));
        Mockito.when(budgetRepository.save(Mockito.any(Budget.class))).thenReturn(updatedBudget);

        Optional<Budget> result = budgetService.updateBudget(1L, updateRequest);

        assertTrue(result.isPresent());
        assertEquals(updatedAt, result.get().getLastUpdatedAt());
        assertEquals(createdAt, result.get().getCreatedAt());
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