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
	    
	    YearMonth expectedMonthYear = YearMonth.of(2000, 1);
	    LocalDateTime now = LocalDateTime.now().withNano(0);

	    Budget expectedBudget = new Budget(3000.0, 1500.0, expectedMonthYear);
	    expectedBudget.setId(1L);
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
		YearMonth expectedMonthYear = YearMonth.of(2000, 1);
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
		String testYearMonthString = "2000-01";
	    YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
	    LocalDateTime now = LocalDateTime.now().withNano(0);
	    
	    Budget budget = new Budget(3000.0, 1500.0, testYearMonth);
	    budget.setCreatedAt(now);
	    budget.setLastUpdatedAt(now);

	    // Mock successful read
	    Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.of(budget));

	    Optional<Budget> retrievedBudget = budgetService.getBudgetByMonthYear(testYearMonthString);

	    assertTrue(retrievedBudget.isPresent());
	    assertEquals(3000.0, retrievedBudget.get().getIncome());
	    assertEquals(1500.0, retrievedBudget.get().getExpenses());
	    assertEquals(1500.0, retrievedBudget.get().getSavings());
	    assertEquals(testYearMonth, retrievedBudget.get().getMonthYear());
	    assertNotNull(retrievedBudget.get().getCreatedAt());
	    assertNotNull(retrievedBudget.get().getLastUpdatedAt());
	}

    
    @Test
    void shouldUpdateBudgetWhenExists() {
    	String testYearMonthString = "2000-01";
        YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        Budget existingBudget = new Budget(3000.0, 1500.0, testYearMonth);
        existingBudget.setCreatedAt(now);
        existingBudget.setLastUpdatedAt(now);

        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setIncome(6000.0);
        updateRequest.setExpenses(3000.0);

        // Mock successful update
        Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.of(existingBudget));
        Mockito.when(budgetRepository.save(Mockito.any(Budget.class))).thenReturn(existingBudget);

        Optional<Budget> updatedBudget = budgetService.updateBudget(testYearMonthString, updateRequest);

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
    	String testYearMonthString = "2000-01";
        YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
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

        Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.of(existingBudget));
        Mockito.when(budgetRepository.save(Mockito.any(Budget.class))).thenReturn(updatedBudget);

        Optional<Budget> result = budgetService.updateBudget(testYearMonthString, updateRequest);

        assertTrue(result.isPresent());
        assertEquals(updatedAt, result.get().getLastUpdatedAt());
        assertEquals(createdAt, result.get().getCreatedAt());
    }

    
    @Test
    void shouldDeleteBudgetWhenExists() {
    	String testYearMonthString = "2000-01";
    	YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
    	
    	// Mock successful delete
    	Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.of(new Budget(3000.0, 1500.0, testYearMonth)));
    	Mockito.doNothing().when(budgetRepository).deleteByMonthYear(testYearMonth);

        boolean deleted = budgetService.deleteBudget(testYearMonthString);

        assertTrue(deleted);
        Mockito.verify(budgetRepository, Mockito.times(1)).deleteByMonthYear(testYearMonth);
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
    	String testYearMonthString = "2000-01";
    	YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
    	
    	// Mock unsuccessful read
    	Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.empty());
        
        Optional<Budget> retrievedBudget = budgetService.getBudgetByMonthYear(testYearMonthString);
        
        assertFalse(retrievedBudget.isPresent());
    }
    
    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentBudget() {
    	String testYearMonthString = "2000-01";
    	YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
    	
        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setIncome(6000.0);
        updateRequest.setExpenses(3000.0);

        // Mock unsuccessful update
        Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.empty());

        Optional<Budget> updatedBudget = budgetService.updateBudget(testYearMonthString, updateRequest);

        assertFalse(updatedBudget.isPresent());
        Mockito.verify(budgetRepository, Mockito.never()).save(Mockito.any(Budget.class));
    }
    
    @Test
    void shouldReturnFalseWhenDeletingNonExistentBudget() {
    	String testYearMonthString = "2000-01";
    	YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
    	
    	// Mock unsuccessful delete
    	Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.empty());
    	
        boolean deleted = budgetService.deleteBudget(testYearMonthString);

        assertFalse(deleted);
        Mockito.verify(budgetRepository, Mockito.never()).deleteByMonthYear(Mockito.any(YearMonth.class));
    }
}