package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.model.Budget;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
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
    void shouldReturnExistingBudgetWhenDuplicateMonthYear() {
        YearMonth existingMonthYear = YearMonth.of(2000, 1);
        
        Budget existingBudget = new Budget(3000.0, 1500.0, existingMonthYear);
        
        BudgetRequest duplicateRequest = new BudgetRequest();
        duplicateRequest.setIncome(4000.0);
        duplicateRequest.setExpenses(2000.0);
        duplicateRequest.setMonthYear(existingMonthYear.toString());

        Mockito.when(budgetRepository.findByMonthYear(existingMonthYear))
               .thenReturn(Optional.of(existingBudget));
        
        Budget shouldBeExistingBudget = budgetService.createBudget(duplicateRequest);
        
	    assertEquals(existingBudget, shouldBeExistingBudget);

	    Mockito.verify(budgetRepository, Mockito.times(0))
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

	    Budget retrievedBudget = budgetService.getBudgetByMonthYear(testYearMonthString);

	    assertNotNull(retrievedBudget);
	    assertEquals(3000.0, retrievedBudget.getIncome());
	    assertEquals(1500.0, retrievedBudget.getExpenses());
	    assertEquals(1500.0, retrievedBudget.getSavings());
	    assertEquals(testYearMonth, retrievedBudget.getMonthYear());
	    assertNotNull(retrievedBudget.getCreatedAt());
	    assertNotNull(retrievedBudget.getLastUpdatedAt());
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

        Budget updatedBudget = budgetService.updateBudget(testYearMonthString, updateRequest);

        assertNotNull(updatedBudget);
        assertEquals(6000.0, updatedBudget.getIncome());
        assertEquals(3000.0, updatedBudget.getExpenses());
        assertEquals(3000.0, updatedBudget.getSavings());
        assertEquals(testYearMonth, updatedBudget.getMonthYear());
        assertNotNull(updatedBudget.getCreatedAt());
        assertNotNull(updatedBudget.getLastUpdatedAt());

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

        Budget result = budgetService.updateBudget(testYearMonthString, updateRequest);

        assertNotNull(result);
        assertEquals(updatedAt, result.getLastUpdatedAt());
        assertEquals(createdAt, result.getCreatedAt());
    }

    
    @Test
    void shouldDeleteBudgetWhenExists() {
    	String testYearMonthString = "2000-01";
    	YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
    	
    	// Mock successful delete
    	Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.of(new Budget(3000.0, 1500.0, testYearMonth)));
    	Mockito.doNothing().when(budgetRepository).deleteByMonthYear(testYearMonth);
    	
    	budgetService.deleteBudget(testYearMonthString);
    	
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
    void shouldReturnNotFoundWhenBudgetDoesNotExist() {
    	String testYearMonthString = "2000-01";
    	YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
    	
    	// Mock unsuccessful read
    	Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.empty());
        
        BudgetNotFoundException thrownException = assertThrows(BudgetNotFoundException.class,
        		() -> budgetService.getBudgetByMonthYear(testYearMonthString));
        
        assertEquals(thrownException.getMessage(), "Budget not found for month: " + testYearMonth);
    }
    
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentBudget() {
    	String testYearMonthString = "2000-01";
    	YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
    	
        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setIncome(6000.0);
        updateRequest.setExpenses(3000.0);

        // Mock unsuccessful update
        Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.empty());
        
        BudgetNotFoundException thrownException = assertThrows(BudgetNotFoundException.class,
        		() -> budgetService.updateBudget(testYearMonthString, updateRequest));
        
        assertEquals(thrownException.getMessage(), "Budget not found for month: " + testYearMonth);
        Mockito.verify(budgetRepository, Mockito.never()).save(Mockito.any(Budget.class));
    }
    
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentBudget() {
    	String testYearMonthString = "2000-01";
    	YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
    	
    	// Mock unsuccessful delete
    	Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.empty());
    	
        BudgetNotFoundException thrownException = assertThrows(BudgetNotFoundException.class,
        		() -> budgetService.deleteBudget(testYearMonthString));

        assertEquals(thrownException.getMessage(), "Budget not found for month: " + testYearMonth);
        Mockito.verify(budgetRepository, Mockito.never()).deleteByMonthYear(Mockito.any(YearMonth.class));
    }
}