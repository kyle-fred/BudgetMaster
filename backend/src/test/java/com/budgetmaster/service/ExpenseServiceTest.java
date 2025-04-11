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
import java.util.List;
import java.util.Optional;

import com.budgetmaster.exception.ExpenseNotFoundException;

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
    	
    	Expense retrievedExpense = expenseService.getExpenseById(1L);
    	
    	assertNotNull(retrievedExpense);   	
        assertEquals("Rent", retrievedExpense.getName());
        assertEquals(1000.0, retrievedExpense.getAmount());
        assertEquals(ExpenseCategory.HOUSING, retrievedExpense.getCategory());
        assertEquals(TransactionType.RECURRING, retrievedExpense.getType());
	    assertEquals(testYearMonth, retrievedExpense.getMonthYear());
	    
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

         Expense updatedExpense = expenseService.updateExpense(1L, updateRequest);

        assertNotNull(updatedExpense);
        assertEquals("GAS BILL", updatedExpense.getName());
        assertEquals(100.0, updatedExpense.getAmount());
        assertEquals(ExpenseCategory.UTILITIES, updatedExpense.getCategory());
        assertEquals(TransactionType.ONE_TIME, updatedExpense.getType());
        
        Mockito.verify(expenseRepository, Mockito.times(1))
        		.saveAndFlush(existingExpense);
    }
    
    @Test
    void shouldDeleteExpenseWhenExists() {
        YearMonth testYearMonth = YearMonth.of(2000, 1);
        Expense existingExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testYearMonth);

        Mockito.when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(existingExpense));
        Mockito.doNothing()
                .when(expenseRepository)
                .deleteById(1L);

        expenseService.deleteExpense(1L);

        Mockito.verify(expenseRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(expenseRepository, Mockito.times(1))
        		.deleteById(1L);
    }
    
    @Test
    void shouldThrowExceptionWhenSaveFails() {
    	String errorMessage = "Duplicate Entry";
        Mockito.when(expenseRepository.saveAndFlush(Mockito.any(Expense.class)))
               .thenThrow(new DataIntegrityViolationException(errorMessage));

        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setName("Rent");
        expenseRequest.setAmount(1000.0);
        expenseRequest.setCategory(ExpenseCategory.HOUSING);
        expenseRequest.setType(TransactionType.RECURRING);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> expenseService.createExpense(expenseRequest),
            errorMessage
        );
    }
    
    @Test
    void shouldThrowExceptionWhenExpenseDoesNotExist() {
    	String errorMessage = "Expense not found with id: 99";
    	Mockito.when(expenseRepository.findById(99L))
    			.thenReturn(Optional.empty());

        ExpenseNotFoundException exception = assertThrows(
        		ExpenseNotFoundException.class,
        		() -> expenseService.getExpenseById(99L),
        		errorMessage
        );

        assertEquals(errorMessage, exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenNoExpensesExistForMonth() {
        YearMonth monthYear = YearMonth.of(2000, 1);
        String errorMessage = "No expenses found for month: " + monthYear;

        try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
            mockedDateUtils.when(() -> DateUtils.getValidYearMonth(monthYear.toString()))
            		.thenReturn(monthYear);
            Mockito.when(expenseRepository.findByMonthYear(monthYear))
            		.thenThrow(new ExpenseNotFoundException(errorMessage));

            assertThrows(ExpenseNotFoundException.class,
                () -> expenseService.getAllExpensesForMonth(monthYear.toString()),
                errorMessage
            );
        }
    }
    
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentExpense() {
    	String errorMessage = "Expense not found with id: 99";

        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Rent");
        updateRequest.setAmount(2000.0);
        updateRequest.setCategory(ExpenseCategory.HOUSING);
        updateRequest.setType(TransactionType.RECURRING);
        
        Mockito.when(expenseRepository.findById(99L))
 				.thenReturn(Optional.empty());

        ExpenseNotFoundException exception = assertThrows(
        		ExpenseNotFoundException.class,
        		() -> expenseService.updateExpense(99L, updateRequest),
        		errorMessage
        );

        assertEquals(errorMessage, exception.getMessage());
        
        Mockito.verify(expenseRepository, Mockito.never())
        		.saveAndFlush(Mockito.any(Expense.class));
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentExpense() {
        String errorMessage = "Expense not found with id: 99";
        Mockito.when(expenseRepository.findById(99L))
                .thenReturn(Optional.empty());

        ExpenseNotFoundException exception = assertThrows(
                ExpenseNotFoundException.class,
                () -> expenseService.deleteExpense(99L),
                errorMessage
        );

        assertEquals(errorMessage, exception.getMessage());
        Mockito.verify(expenseRepository, Mockito.never())
        		.deleteById(Mockito.anyLong());
    }
}