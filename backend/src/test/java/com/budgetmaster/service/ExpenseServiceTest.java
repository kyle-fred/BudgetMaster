package com.budgetmaster.service;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.enums.ExpenseType;
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
        expectedExpense.setTarget("Estate Agent XYZ");
        expectedExpense.setAmount(1000.0);
        expectedExpense.setExpenseType(ExpenseType.RECURRING);
    	
        // Mock successful creation
        Mockito.when(expenseRepository.save(Mockito.any(Expense.class)))
        	.thenReturn(expectedExpense);
        
        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setName("Rent");
        expenseRequest.setTarget("Estate Agent XYZ");
        expenseRequest.setAmount(1000.0);
        expenseRequest.setExpenseType(ExpenseType.RECURRING);
        
        Expense savedExpense = expenseService.createExpense(expenseRequest);

        assertNotNull(savedExpense);
        assertEquals(1L, savedExpense.getId());
        assertEquals("Rent", savedExpense.getName());
        assertEquals("Estate Agent XYZ", savedExpense.getTarget());
        assertEquals(1000.0, savedExpense.getAmount());
        assertEquals(ExpenseType.RECURRING, savedExpense.getExpenseType());

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
        expenseRequest.setTarget("Estate Agent XYZ");
        expenseRequest.setAmount(1000.0);
        expenseRequest.setExpenseType(ExpenseType.RECURRING);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> expenseService.createExpense(expenseRequest));
    }
    
    @Test
    void shouldReturnExpenseWhenExists() {
    	Expense expense = new Expense();
    	expense.setId(1L);
    	expense.setName("Rent");
    	expense.setTarget("Estate Agent XYZ");
    	expense.setAmount(1000.0);
    	expense.setExpenseType(ExpenseType.RECURRING);
    	
    	// Mock successful read
    	Mockito.when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
    	
    	Optional<Expense> retrievedExpense = expenseService.getExpenseById(1L);
    	
    	assertTrue(retrievedExpense.isPresent());
    	assertEquals(1L, retrievedExpense.get().getId());    	
        assertEquals("Rent", retrievedExpense.get().getName());
        assertEquals("Estate Agent XYZ", retrievedExpense.get().getTarget());
        assertEquals(1000.0, retrievedExpense.get().getAmount());
        assertEquals(ExpenseType.RECURRING, retrievedExpense.get().getExpenseType());
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
        existingExpense.setTarget("Estate Agent XYZ");
        existingExpense.setAmount(1000.0);
        existingExpense.setExpenseType(ExpenseType.RECURRING);

        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Debt Repayment");
        updateRequest.setTarget("Bank XYZ");
        updateRequest.setAmount(100.0);
        updateRequest.setExpenseType(ExpenseType.ONE_TIME);

        // Mock successful update
        Mockito.when(expenseRepository.findById(1L)).thenReturn(Optional.of(existingExpense));
        Mockito.when(expenseRepository.save(Mockito.any(Expense.class))).thenReturn(existingExpense);

        Optional<Expense> updatedExpense = expenseService.updateExpense(1L, updateRequest);

        assertTrue(updatedExpense.isPresent());
        assertEquals(1L, updatedExpense.get().getId());
        assertEquals("Debt Repayment", updatedExpense.get().getName());
        assertEquals("Bank XYZ", updatedExpense.get().getTarget());
        assertEquals(100.0, updatedExpense.get().getAmount());
        assertEquals(ExpenseType.ONE_TIME, updatedExpense.get().getExpenseType());
        
        Mockito.verify(expenseRepository, Mockito.times(1)).save(existingExpense);
    }
    
    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentExpense() {
        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Rent");
        updateRequest.setTarget("Estate Agent XYZ");
        updateRequest.setAmount(2000.0);
        updateRequest.setExpenseType(ExpenseType.RECURRING);

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