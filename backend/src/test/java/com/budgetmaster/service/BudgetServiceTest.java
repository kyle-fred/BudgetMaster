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
	    double expectedIncome = 3000.0;
	    double expectedExpense = 1500.0;
	    double expectedSavings = expectedIncome - expectedExpense;

	    Budget expectedBudget = new Budget(expectedIncome, expectedExpense, expectedMonthYear);
	    expectedBudget.setSavings(expectedSavings);
	    
	    Mockito.when(budgetRepository.saveAndFlush(Mockito.any(Budget.class)))
	            .thenReturn(expectedBudget);
	    
	    BudgetRequest budgetRequest = new BudgetRequest();
	    budgetRequest.setTotalIncome(expectedIncome);
	    budgetRequest.setTotalExpense(expectedExpense);
	    budgetRequest.setMonthYear(expectedMonthYear.toString());

	    Budget savedBudget = budgetService.createBudget(budgetRequest);

	    assertNotNull(savedBudget);
	    assertEquals(expectedIncome, savedBudget.getTotalIncome());
	    assertEquals(expectedExpense, savedBudget.getTotalExpense());
	    assertEquals(expectedSavings, savedBudget.getSavings());
	    assertEquals(expectedMonthYear, savedBudget.getMonthYear());

	    Mockito.verify(budgetRepository, Mockito.times(1))
	            .saveAndFlush(Mockito.any(Budget.class));
	}
	
    @Test
    void shouldReturnExistingBudgetWhenDuplicateMonthYear() {
        YearMonth existingMonthYear = YearMonth.of(2000, 1);
        double expectedIncome = 3000.0;
        double expectedExpense = 1500.0;
        double expectedSavings = expectedIncome - expectedExpense;
        
        Budget existingBudget = new Budget(expectedIncome, expectedExpense, existingMonthYear);
        existingBudget.setSavings(expectedSavings);
        
        BudgetRequest duplicateRequest = new BudgetRequest();
        duplicateRequest.setTotalIncome(4000.0);
        duplicateRequest.setTotalExpense(2000.0);
        duplicateRequest.setMonthYear(existingMonthYear.toString());

        Mockito.when(budgetRepository.findByMonthYear(existingMonthYear))
               .thenReturn(Optional.of(existingBudget));
        
        Budget shouldBeExistingBudget = budgetService.createBudget(duplicateRequest);
        
	    assertEquals(existingBudget, shouldBeExistingBudget);
	    assertEquals(expectedSavings, shouldBeExistingBudget.getSavings());

	    Mockito.verify(budgetRepository, Mockito.times(0))
	            .saveAndFlush(Mockito.any(Budget.class));
    }
    
	@Test
	void shouldReturnBudgetWhenExists() {
		String testYearMonthString = "2000-01";
	    YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
	    double expectedIncome = 3000.0;
	    double expectedExpense = 1500.0;
	    double expectedSavings = expectedIncome - expectedExpense;
	    
	    Budget budget = new Budget(expectedIncome, expectedExpense, testYearMonth);
	    budget.setSavings(expectedSavings);

	    Mockito.when(budgetRepository.findByMonthYear(testYearMonth))
                .thenReturn(Optional.of(budget));

	    Budget retrievedBudget = budgetService.getBudgetByMonthYear(testYearMonthString);

	    assertNotNull(retrievedBudget);
	    assertEquals(expectedIncome, retrievedBudget.getTotalIncome());
	    assertEquals(expectedExpense, retrievedBudget.getTotalExpense());
	    assertEquals(expectedSavings, retrievedBudget.getSavings());
	    assertEquals(testYearMonth, retrievedBudget.getMonthYear());
	}
    
    @Test
    void shouldUpdateBudgetWhenExists() {
    	Long budgetId = 1L;
        YearMonth testYearMonth = YearMonth.parse("2000-01");
        double expectedIncome = 6000.0;
        double expectedExpense = 3000.0;
        double expectedSavings = expectedIncome - expectedExpense;

        Budget existingBudget = new Budget(3000.0, 1500.0, testYearMonth);
        existingBudget.setId(budgetId);
        existingBudget.setSavings(expectedSavings);

        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setTotalIncome(expectedIncome);
        updateRequest.setTotalExpense(expectedExpense);

        Mockito.when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.of(existingBudget));
        Mockito.when(budgetRepository.saveAndFlush(Mockito.any(Budget.class)))
                .thenReturn(existingBudget);

        Budget updatedBudget = budgetService.updateBudget(budgetId, updateRequest);

        assertNotNull(updatedBudget);
        assertEquals(expectedIncome, updatedBudget.getTotalIncome());
        assertEquals(expectedExpense, updatedBudget.getTotalExpense());
        assertEquals(expectedSavings, updatedBudget.getSavings());
        assertEquals(testYearMonth, updatedBudget.getMonthYear());

        Mockito.verify(budgetRepository, Mockito.times(1)).saveAndFlush(existingBudget);
    }
    
    @Test
    void shouldDeleteBudgetWhenExists() {
    	Long budgetId = 1L;
    	YearMonth testYearMonth = YearMonth.parse("2000-01");
    	double expectedIncome = 3000.0;
    	double expectedExpense = 1500.0;
    	double expectedSavings = expectedIncome - expectedExpense;
    	
    	Budget existingBudget = new Budget(expectedIncome, expectedExpense, testYearMonth);
    	existingBudget.setId(budgetId);
    	existingBudget.setSavings(expectedSavings);
    	
    	Mockito.when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.of(existingBudget));
    	Mockito.doNothing()
                .when(budgetRepository)
                .deleteById(budgetId);
    	
    	budgetService.deleteBudget(budgetId);
    	
        Mockito.verify(budgetRepository, Mockito.times(1)).deleteById(budgetId);
    }
    
    @Test
    void shouldThrowExceptionWhenSaveFails() {
        Mockito.when(budgetRepository.saveAndFlush(Mockito.any(Budget.class)))
               .thenThrow(new DataIntegrityViolationException("Duplicate Entry"));

        BudgetRequest budgetRequest = new BudgetRequest();
        budgetRequest.setTotalIncome(3000.0);
        budgetRequest.setTotalExpense(1500.0);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> budgetService.createBudget(budgetRequest));
    }
    
    @Test
    void shouldReturnNotFoundWhenBudgetDoesNotExist() {
    	String testYearMonthString = "2000-01";
    	YearMonth testYearMonth = YearMonth.parse(testYearMonthString);
    	
    	Mockito.when(budgetRepository.findByMonthYear(testYearMonth)).thenReturn(Optional.empty());
        
        BudgetNotFoundException thrownException = assertThrows(BudgetNotFoundException.class,
        		() -> budgetService.getBudgetByMonthYear(testYearMonthString));
        
        assertEquals(thrownException.getMessage(), "Budget not found for month: " + testYearMonth);
    }
    
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentBudget() {
    	Long budgetId = 1L;
    	
        BudgetRequest updateRequest = new BudgetRequest();
        updateRequest.setTotalIncome(6000.0);
        updateRequest.setTotalExpense(3000.0);

        Mockito.when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.empty());
        
        BudgetNotFoundException thrownException = assertThrows(BudgetNotFoundException.class,
        		() -> budgetService.updateBudget(budgetId, updateRequest));
        
        assertEquals(thrownException.getMessage(), "Budget not found for id: " + budgetId);
        Mockito.verify(budgetRepository, Mockito.never())
                .saveAndFlush(Mockito.any(Budget.class));
    }
    
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentBudget() {
    	Long budgetId = 1L;
    	
    	Mockito.when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());
    	
        BudgetNotFoundException thrownException = assertThrows(BudgetNotFoundException.class,
        		() -> budgetService.deleteBudget(budgetId));

        assertEquals(thrownException.getMessage(), "Budget not found for id: " + budgetId);
        Mockito.verify(budgetRepository, Mockito.never())
                .deleteById(Mockito.anyLong());
    }
}