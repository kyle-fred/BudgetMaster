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

import java.time.YearMonth;
import java.util.Optional;

class BudgetServiceTest {
	
	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);
	
	@Test
	void shouldCreateAndSaveBudgetSuccessfully() {
	    
	    YearMonth expectedMonthYear = YearMonth.of(2000, 1);

	    Budget expectedBudget = new Budget(3000.0, 1500.0, expectedMonthYear);
	    
	    // Mock successful creation
	    Mockito.when(budgetRepository.saveAndFlush(Mockito.any(Budget.class)))
	        .thenReturn(expectedBudget);
	    
	    BudgetRequest budgetRequest = new BudgetRequest();
	    budgetRequest.setTotalIncome(3000.0);
	    budgetRequest.setTotalExpenses(1500.0);
	    budgetRequest.setMonthYear(expectedMonthYear.toString());

	    Budget savedBudget = budgetService.createBudget(budgetRequest);

	    assertNotNull(savedBudget);
	    assertEquals(3000.0, savedBudget.getTotalIncome());
	    assertEquals(1500.0, savedBudget.getTotalExpenses());
	    assertEquals(1500.0, savedBudget.getSavings());
	    assertEquals(expectedMonthYear, savedBudget.getMonthYear());

	    Mockito.verify(budgetRepository, Mockito.times(1))
	        .saveAndFlush(Mockito.any(Budget.class));
	}
	
    @Test
    void shouldReturnExistingBudgetWhenDuplicateMonthYear() {
        YearMonth existingMonthYear = YearMonth.of(2000, 1);
        
        Budget existingBudget = new Budget(3000.0, 1500.0, existingMonthYear);
        
        BudgetRequest duplicateRequest = new BudgetRequest();
        duplicateRequest.setTotalIncome(4000.0);
        duplicateRequest.setTotalExpenses(2000.0);
        duplicateRequest.setMonthYear(existingMonthYear.toString());

        Mockito.when(budgetRepository.findByMonthYear(existingMonthYear))
               .thenReturn(Optional.of(existingBudget));
        
        Budget shouldBeExistingBudget = budgetService.createBudget(duplicateRequest);
        
	    assertEquals(existingBudget, shouldBeExistingBudget);

	    Mockito.verify(budgetRepository, Mockito.times(0))
	        .saveAndFlush(Mockito.any(Budget.class));
    }
    
	@Test
	void shouldReturnBudgetWhenExists() {
		String testYearMonthString = "2000-01";
	    YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
	    
	    Budget budget = new Budget(3000.0, 1500.0, testYearMonth);

	    // Mock successful read
	    Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.of(budget));

	    Budget retrievedBudget = budgetService.getBudgetByMonthYear(testYearMonthString);

	    assertNotNull(retrievedBudget);
	    assertEquals(3000.0, retrievedBudget.getTotalIncome());
	    assertEquals(1500.0, retrievedBudget.getTotalExpenses());
	    assertEquals(1500.0, retrievedBudget.getSavings());
	    assertEquals(testYearMonth, retrievedBudget.getMonthYear());
	}

    
    @Test
    void shouldUpdateBudgetWhenExists() {
    	String testYearMonthString = "2000-01";
        YearMonth testYearMonth = YearMonth.parse(testYearMonthString);

        Budget existingBudget = new Budget(3000.0, 1500.0, testYearMonth);

        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setTotalIncome(6000.0);
        updateRequest.setTotalExpenses(3000.0);

        // Mock successful update
        Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.of(existingBudget));
        Mockito.when(budgetRepository.saveAndFlush(Mockito.any(Budget.class))).thenReturn(existingBudget);

        Budget updatedBudget = budgetService.updateBudget(testYearMonthString, updateRequest);

        assertNotNull(updatedBudget);
        assertEquals(6000.0, updatedBudget.getTotalIncome());
        assertEquals(3000.0, updatedBudget.getTotalExpenses());
        assertEquals(3000.0, updatedBudget.getSavings());
        assertEquals(testYearMonth, updatedBudget.getMonthYear());

        Mockito.verify(budgetRepository, Mockito.times(1)).saveAndFlush(existingBudget);
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
        Mockito.when(budgetRepository.saveAndFlush(Mockito.any(Budget.class)))
               .thenThrow(new DataIntegrityViolationException("Duplicate Entry"));

        BudgetRequest budgetRequest = new BudgetRequest();
        budgetRequest.setTotalIncome(3000.0);
        budgetRequest.setTotalExpenses(1500.0);
        
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
        updateRequest.setTotalIncome(6000.0);
        updateRequest.setTotalExpenses(3000.0);

        // Mock unsuccessful update
        Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.empty());
        
        BudgetNotFoundException thrownException = assertThrows(BudgetNotFoundException.class,
        		() -> budgetService.updateBudget(testYearMonthString, updateRequest));
        
        assertEquals(thrownException.getMessage(), "Budget not found for month: " + testYearMonth);
        Mockito.verify(budgetRepository, Mockito.never()).saveAndFlush(Mockito.any(Budget.class));
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