package com.budgetmaster.service;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.repository.ExpenseRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.model.Expense;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ExpenseServiceTest {
	
	private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
	private final ExpenseService expenseService = new ExpenseService(expenseRepository);
	
    @Test
    void shouldCreateAndSaveExpenseSuccessfully() {
	    YearMonth expectedMonthYear = YearMonth.of(2000, 1);
    	Expense expectedExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, expectedMonthYear);
        
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
        assertEquals("Rent", savedExpense.getName());
        assertEquals(1000.0, savedExpense.getAmount());
        assertEquals(ExpenseCategory.HOUSING, savedExpense.getCategory());
        assertEquals(TransactionType.RECURRING, savedExpense.getType());
	    assertEquals(expectedMonthYear, savedExpense.getMonthYear());

        Mockito.verify(expenseRepository, Mockito.times(1))
        		.saveAndFlush(Mockito.any(Expense.class));
    }
    
    @Test
    void shouldReturnListOfExpensesWhenMonthHasExpenses() {
        YearMonth monthYear = YearMonth.of(2000, 1);

 		List<Expense> expenseList = List.of(
 				new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, monthYear),
 				new Expense("Gas", 100.0, ExpenseCategory.UTILITIES, TransactionType.RECURRING, monthYear)
 		);

        try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
            mockedDateUtils.when(() -> DateUtils.getValidYearMonth(monthYear.toString()))
            		.thenReturn(monthYear);
            Mockito.when(expenseRepository.findByMonthYear(monthYear))
            		.thenReturn(expenseList);

            List<Expense> result = expenseService.getAllExpensesForMonth(monthYear.toString());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Rent", result.get(0).getName());
            assertEquals("Gas", result.get(1).getName());

            Mockito.verify(expenseRepository, Mockito.times(1)).findByMonthYear(monthYear);
        }
    }
    
    @Test
    void shouldReturnExpenseWhenExists() {
	    YearMonth testYearMonth = YearMonth.of(2000, 1);
    	Expense expectedExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testYearMonth);
    	
    	Mockito.when(expenseRepository.findById(Mockito.eq(1L)))
    			.thenReturn(Optional.of(expectedExpense));
    	
    	Optional<Expense> retrievedExpense = expenseService.getExpenseById(1L);
    	
    	assertTrue(retrievedExpense.isPresent());   	
        assertEquals("Rent", retrievedExpense.get().getName());
        assertEquals(1000.0, retrievedExpense.get().getAmount());
        assertEquals(ExpenseCategory.HOUSING, retrievedExpense.get().getCategory());
        assertEquals(TransactionType.RECURRING, retrievedExpense.get().getType());
	    assertEquals(testYearMonth, retrievedExpense.get().getMonthYear());
	    
        Mockito.verify(expenseRepository, Mockito.times(1))
				.findById(Mockito.eq(1L));
    }
    
    @Test
    void shouldUpdateExpenseWhenExists() {
    	YearMonth testYearMonth = YearMonth.of(2000, 1); 	    
    	Expense existingExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testYearMonth);

        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Gas Bill");
        updateRequest.setAmount(100.0);
        updateRequest.setCategory(ExpenseCategory.UTILITIES);
        updateRequest.setType(TransactionType.ONE_TIME);
        
        Mockito.when(expenseRepository.findById(Mockito.eq(1L)))
 				.thenReturn(Optional.of(existingExpense));
         Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
         		.thenReturn(existingExpense);

         Optional<Expense> updatedExpense = expenseService.updateExpense(1L, updateRequest);

        assertTrue(updatedExpense.isPresent());
        Expense result = updatedExpense.get();
        assertEquals("GAS BILL", result.getName());
        assertEquals(100.0, result.getAmount());
        assertEquals(ExpenseCategory.UTILITIES, result.getCategory());
        assertEquals(TransactionType.ONE_TIME, result.getType());
        
        Mockito.verify(expenseRepository, Mockito.times(1))
        		.saveAndFlush(existingExpense);
    }
    
    @Test
    void shouldDeleteExpenseWhenExists() {
    	Mockito.doReturn(true)
				.when(expenseRepository)
				.existsById(1L);
    	Mockito.doNothing()
				.when(expenseRepository)
				.deleteById(1L);
    	
        boolean deleted = expenseService.deleteExpense(1L);

        assertTrue(deleted);
        
        Mockito.verify(expenseRepository, Mockito.times(1))
 				.deleteById(1L);
    }
    
    @Test
    void shouldThrowExceptionWhenSaveFails() {
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
    	Mockito.when(expenseRepository.findById(99L))
				.thenReturn(Optional.empty());
        
    	Optional<Expense> retrievedExpense = expenseService.getExpenseById(99L);
        
        assertFalse(retrievedExpense.isPresent());
    }
    
    @Test
    void shouldReturnEmptyListWhenNoExpensesExistForMonth() {
        YearMonth monthYear = YearMonth.of(2000, 1);
        
        try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
            mockedDateUtils.when(() -> DateUtils.getValidYearMonth(monthYear.toString()))
            		.thenReturn(monthYear);
            Mockito.when(expenseRepository.findByMonthYear(monthYear))
            		.thenReturn(Collections.emptyList());
            
            List<Expense> result = expenseService.getAllExpensesForMonth(monthYear.toString());

            assertNotNull(result);
            assertTrue(result.isEmpty());

            Mockito.verify(expenseRepository, Mockito.times(1)).findByMonthYear(monthYear);
        }
    }
    
    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentExpense() {
    	ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Rent");
        updateRequest.setAmount(2000.0);
        updateRequest.setCategory(ExpenseCategory.HOUSING);
        updateRequest.setType(TransactionType.RECURRING);
        
        Mockito.when(expenseRepository.findById(99L))
 				.thenReturn(Optional.empty());

        Optional<Expense> updatedExpense = expenseService.updateExpense( 99L, updateRequest);

        assertFalse(updatedExpense.isPresent());
        
        Mockito.verify(expenseRepository, Mockito.never())
        		.saveAndFlush(Mockito.any(Expense.class));
    }
    
    @Test
    void shouldReturnFalseWhenDeletingNonExistentExpense() {
    	Mockito.doReturn(false)
				.when(expenseRepository)
				.existsById(99L);
        
        boolean deleted = expenseService.deleteExpense(99L);

        assertFalse(deleted);
        
        Mockito.verify(expenseRepository, Mockito.never())
 				.deleteById(Mockito.anyLong());
    }
}