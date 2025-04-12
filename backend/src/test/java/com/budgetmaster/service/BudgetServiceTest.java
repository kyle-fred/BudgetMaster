package com.budgetmaster.service;

import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.model.Budget;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.time.YearMonth;
import java.util.Optional;

class BudgetServiceTest {
	
	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);
    
	@Test
	void shouldReturnBudgetWhenExists() {
		String testMonthString = "2000-01";
	    YearMonth testMonth = YearMonth.parse(testMonthString);
	    double expectedIncome = 3000.0;
	    double expectedExpense = 1500.0;
	    double expectedSavings = expectedIncome - expectedExpense;
	    
	    Budget budget = new Budget(testMonth);
	    budget.setTotalIncome(expectedIncome);
	    budget.setTotalExpense(expectedExpense);
	    budget.setSavings(expectedSavings);

	    Mockito.when(budgetRepository.findByMonth(testMonth))
                .thenReturn(Optional.of(budget));

	    Budget retrievedBudget = budgetService.getBudgetByMonth(testMonthString);

	    assertNotNull(retrievedBudget);
	    assertEquals(expectedIncome, retrievedBudget.getTotalIncome());
	    assertEquals(expectedExpense, retrievedBudget.getTotalExpense());
	    assertEquals(expectedSavings, retrievedBudget.getSavings());
	    assertEquals(testMonth, retrievedBudget.getMonth());
	}
    
    @Test
    void shouldDeleteBudgetWhenExists() {
    	Long budgetId = 1L;
    	YearMonth testMonth = YearMonth.parse("2000-01");
    	double expectedIncome = 3000.0;
    	double expectedExpense = 1500.0;
    	double expectedSavings = expectedIncome - expectedExpense;
    	
    	Budget existingBudget = new Budget(testMonth);
    	existingBudget.setId(budgetId);
    	existingBudget.setTotalIncome(expectedIncome);
    	existingBudget.setTotalExpense(expectedExpense);
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
    void shouldThrowExceptionWhenBudgetDoesNotExist() {
        String testMonthString = "2000-01";
    	String errorMessage = "Budget not found for month: " + testMonthString;
    	YearMonth testMonth = YearMonth.parse(testMonthString);
    	
    	Mockito.when(budgetRepository.findByMonth(testMonth))
				.thenReturn(Optional.empty());
        
        BudgetNotFoundException thrownException = assertThrows(BudgetNotFoundException.class,
        		() -> budgetService.getBudgetByMonth(testMonthString),
        		errorMessage
        );
        
        assertEquals(errorMessage, thrownException.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentBudget() {
    	Long budgetId = 1L;
    	String errorMessage = "Budget not found for id: " + budgetId;
    	
    	Mockito.when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());
    	
        BudgetNotFoundException thrownException = assertThrows(BudgetNotFoundException.class,
        		() -> budgetService.deleteBudget(budgetId),
        		errorMessage
        );

        assertEquals(errorMessage, thrownException.getMessage());
        Mockito.verify(budgetRepository, Mockito.never())
                .deleteById(Mockito.anyLong());
    }
}