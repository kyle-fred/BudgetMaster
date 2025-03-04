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
}