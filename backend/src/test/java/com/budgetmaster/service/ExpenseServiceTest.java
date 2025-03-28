package com.budgetmaster.service;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.repository.ExpenseRepository;
import com.budgetmaster.model.Expense;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.time.YearMonth;
import java.util.Optional;

class ExpenseServiceTest {
	
	private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
	private final ExpenseService expenseService = new ExpenseService(expenseRepository);
	
    @Test
    void shouldCreateAndSaveExpenseSuccessfully() {
        
	    YearMonth expectedMonthYear = YearMonth.of(2000, 1);
    	
    	Expense expectedExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, expectedMonthYear);
        expectedExpense.setId(1L);
    	
        // Mock successful creation
        Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
        	.thenReturn(expectedExpense);
        
        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setName("Rent");
        expenseRequest.setAmount(1000.0);
        expenseRequest.setCategory(ExpenseCategory.HOUSING);
        expenseRequest.setType(TransactionType.RECURRING);
        expenseRequest.setMonthYear(expectedMonthYear.toString());
        
        Expense savedExpense = expenseService.createExpense(expenseRequest);

        assertNotNull(savedExpense);
        assertEquals(1L, savedExpense.getId());
        assertEquals("Rent", savedExpense.getName());
        assertEquals(1000.0, savedExpense.getAmount());
        assertEquals(ExpenseCategory.HOUSING, savedExpense.getCategory());
        assertEquals(TransactionType.RECURRING, savedExpense.getType());
	    assertEquals(expectedMonthYear, savedExpense.getMonthYear());

        Mockito.verify(expenseRepository, Mockito.times(1))
        	.saveAndFlush(Mockito.any(Expense.class));
    }
    
    @Test
    void shouldReturnExpenseWhenExists() {
    	
	    YearMonth testYearMonth = YearMonth.of(2000, 1);
    	
    	Expense expectedExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testYearMonth);
    	expectedExpense.setId(1L);
    	
    	// Mock successful read
    	Mockito.when(expenseRepository.findById(1L)).thenReturn(Optional.of(expectedExpense));
    	
    	Optional<Expense> retrievedExpense = expenseService.getExpenseById(1L);
    	
    	assertTrue(retrievedExpense.isPresent());
    	assertEquals(1L, retrievedExpense.get().getId());    	
        assertEquals("Rent", retrievedExpense.get().getName());
        assertEquals(1000.0, retrievedExpense.get().getAmount());
        assertEquals(ExpenseCategory.HOUSING, retrievedExpense.get().getCategory());
        assertEquals(TransactionType.RECURRING, retrievedExpense.get().getType());
	    assertEquals(testYearMonth, retrievedExpense.get().getMonthYear());
    }
    
    @Test
    void shouldUpdateExpenseWhenExists() {
	    
    	YearMonth testYearMonth = YearMonth.of(2000, 1);
 	    
    	Expense existingExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testYearMonth);
    	existingExpense.setId(1L);

        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Gas Bill");
        updateRequest.setAmount(100.0);
        updateRequest.setCategory(ExpenseCategory.UTILITIES);
        updateRequest.setType(TransactionType.ONE_TIME);

        // Mock successful update
        Mockito.when(expenseRepository.findById(1L)).thenReturn(Optional.of(existingExpense));
        Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class))).thenReturn(existingExpense);

        Optional<Expense> updatedExpense = expenseService.updateExpense(1L, updateRequest);

        assertTrue(updatedExpense.isPresent());
        assertEquals(1L, updatedExpense.get().getId());
        assertEquals("Gas Bill", updatedExpense.get().getName());
        assertEquals(100.0, updatedExpense.get().getAmount());
        assertEquals(ExpenseCategory.UTILITIES, updatedExpense.get().getCategory());
        assertEquals(TransactionType.ONE_TIME, updatedExpense.get().getType());
        
        Mockito.verify(expenseRepository, Mockito.times(1)).saveAndFlush(existingExpense);
    }
    
    @Test
    void shouldDeleteExpenseWhenExists() {
    	
    	// Mock successful delete
    	Mockito.when(expenseRepository.existsById(1L)).thenReturn(true);
    	Mockito.doNothing().when(expenseRepository).deleteById(1L);

        boolean deleted = expenseService.deleteExpense(1L);

        assertTrue(deleted);
        Mockito.verify(expenseRepository, Mockito.times(1)).deleteById(1L);
    }
    
    @Test
    void shouldThrowExceptionWhenSaveFails() {
    	
    	// Mock unsuccessful creation
        Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
               .thenThrow(new DataIntegrityViolationException("Duplicate Entry"));

        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setName("Rent");
        expenseRequest.setAmount(1000.0);
        expenseRequest.setCategory(ExpenseCategory.HOUSING);
        expenseRequest.setType(TransactionType.RECURRING);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> expenseService.createExpense(expenseRequest));
    }
    
    @Test
    void shouldReturnEmptyWhenExpenseDoesNotExist() {
    	
    	// Mock unsuccessful read
    	Mockito.when(expenseRepository.findById(99L)).thenReturn(Optional.empty());
        
        Optional<Expense> retrievedExpense = expenseService.getExpenseById(99L);
        
        assertFalse(retrievedExpense.isPresent());
    }
    
    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentExpense() {
        
    	ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Rent");
        updateRequest.setAmount(2000.0);
        updateRequest.setCategory(ExpenseCategory.HOUSING);
        updateRequest.setType(TransactionType.RECURRING);

        // Mock unsuccessful update
        Mockito.when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Expense> updatedExpense = expenseService.updateExpense(99L, updateRequest);

        assertFalse(updatedExpense.isPresent());
        Mockito.verify(expenseRepository, Mockito.never()).saveAndFlush(Mockito.any(Expense.class));
    }
    
    @Test
    void shouldReturnFalseWhenDeletingNonExistentExpense() {
    	
    	// Mock unsuccessful delete
    	Mockito.when(expenseRepository.existsById(99L)).thenReturn(false);

        boolean deleted = expenseService.deleteExpense(99L);

        assertFalse(deleted);
        Mockito.verify(expenseRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}