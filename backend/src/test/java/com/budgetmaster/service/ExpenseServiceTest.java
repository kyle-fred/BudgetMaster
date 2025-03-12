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

import java.util.Optional;

class ExpenseServiceTest {
	
	private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
	private final ExpenseService expenseService = new ExpenseService(expenseRepository);
	
    @Test
    void shouldCreateAndSaveExpenseToDbSuccessfully() {
        
    	Expense expectedExpense = new Expense();
        expectedExpense.setId(1L);
        expectedExpense.setName("Rent");
        expectedExpense.setAmount(1000.0);
        expectedExpense.setExpenseCategory(ExpenseCategory.HOUSING);
        expectedExpense.setTransactionType(TransactionType.RECURRING);
    	
        // Mock successful creation
        Mockito.when(expenseRepository.save(Mockito.any(Expense.class)))
        	.thenReturn(expectedExpense);
        
        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setName("Rent");
        expenseRequest.setAmount(1000.0);
        expenseRequest.setExpenseCategory(ExpenseCategory.HOUSING);
        expenseRequest.setTransactionType(TransactionType.RECURRING);
        
        Expense savedExpense = expenseService.createExpense(expenseRequest);

        assertNotNull(savedExpense);
        assertEquals(1L, savedExpense.getId());
        assertEquals("Rent", savedExpense.getName());
        assertEquals(1000.0, savedExpense.getAmount());
        assertEquals(ExpenseCategory.HOUSING, savedExpense.getExpenseCategory());
        assertEquals(TransactionType.RECURRING, savedExpense.getTransactionType());

        Mockito.verify(expenseRepository, Mockito.times(1))
        	.save(Mockito.any(Expense.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaveFails() {
    	// Mock unsuccessful creation
        Mockito.when(expenseRepository.save(Mockito.any(Expense.class)))
               .thenThrow(new DataIntegrityViolationException("Duplicate Entry"));

        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setName("Rent");
        expenseRequest.setAmount(1000.0);
        expenseRequest.setExpenseCategory(ExpenseCategory.HOUSING);
        expenseRequest.setTransactionType(TransactionType.RECURRING);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> expenseService.createExpense(expenseRequest));
    }
    
    @Test
    void shouldReturnExpenseWhenExists() {
    	Expense expense = new Expense();
    	expense.setId(1L);
    	expense.setName("Rent");
    	expense.setAmount(1000.0);
    	expense.setExpenseCategory(ExpenseCategory.HOUSING);
    	expense.setTransactionType(TransactionType.RECURRING);
    	
    	// Mock successful read
    	Mockito.when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
    	
    	Optional<Expense> retrievedExpense = expenseService.getExpenseById(1L);
    	
    	assertTrue(retrievedExpense.isPresent());
    	assertEquals(1L, retrievedExpense.get().getId());    	
        assertEquals("Rent", retrievedExpense.get().getName());
        assertEquals(1000.0, retrievedExpense.get().getAmount());
        assertEquals(ExpenseCategory.HOUSING, retrievedExpense.get().getExpenseCategory());
        assertEquals(TransactionType.RECURRING, retrievedExpense.get().getTransactionType());
    }
    
    @Test
    void shouldReturnEmptyWhenExpenseDoesNotExist() {
    	// Mock unsuccessful read
    	Mockito.when(expenseRepository.findById(99L)).thenReturn(Optional.empty());
        
        Optional<Expense> retrievedExpense = expenseService.getExpenseById(99L);
        
        assertFalse(retrievedExpense.isPresent());
    }
    
    @Test
    void shouldUpdateExpenseWhenExists() {
        Expense existingExpense = new Expense();
        existingExpense.setId(1L);
        existingExpense.setName("Rent");
        existingExpense.setAmount(1000.0);
        existingExpense.setExpenseCategory(ExpenseCategory.HOUSING);
        existingExpense.setTransactionType(TransactionType.RECURRING);

        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Gas Bill");
        updateRequest.setAmount(100.0);
        updateRequest.setExpenseCategory(ExpenseCategory.UTILITIES);
        updateRequest.setTransactionType(TransactionType.ONE_TIME);

        // Mock successful update
        Mockito.when(expenseRepository.findById(1L)).thenReturn(Optional.of(existingExpense));
        Mockito.when(expenseRepository.save(Mockito.any(Expense.class))).thenReturn(existingExpense);

        Optional<Expense> updatedExpense = expenseService.updateExpense(1L, updateRequest);

        assertTrue(updatedExpense.isPresent());
        assertEquals(1L, updatedExpense.get().getId());
        assertEquals("Gas Bill", updatedExpense.get().getName());
        assertEquals(100.0, updatedExpense.get().getAmount());
        assertEquals(ExpenseCategory.UTILITIES, updatedExpense.get().getExpenseCategory());
        assertEquals(TransactionType.ONE_TIME, updatedExpense.get().getTransactionType());
        
        Mockito.verify(expenseRepository, Mockito.times(1)).save(existingExpense);
    }
    
    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentExpense() {
        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Rent");
        updateRequest.setAmount(2000.0);
        updateRequest.setExpenseCategory(ExpenseCategory.HOUSING);
        updateRequest.setTransactionType(TransactionType.RECURRING);

        // Mock unsuccessful update
        Mockito.when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Expense> updatedExpense = expenseService.updateExpense(99L, updateRequest);

        assertFalse(updatedExpense.isPresent());
        Mockito.verify(expenseRepository, Mockito.never()).save(Mockito.any(Expense.class));
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
    void shouldReturnFalseWhenDeletingNonExistentExpense() {
    	// Mock unsuccessful delete
    	Mockito.when(expenseRepository.existsById(99L)).thenReturn(false);

        boolean deleted = expenseService.deleteExpense(99L);

        assertFalse(deleted);
        Mockito.verify(expenseRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}