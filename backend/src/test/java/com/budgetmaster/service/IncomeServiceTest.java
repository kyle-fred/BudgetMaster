package com.budgetmaster.service;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.repository.IncomeRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.model.Income;
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

import com.budgetmaster.exception.IncomeNotFoundException;

class IncomeServiceTest {
	
	private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
	private final IncomeService incomeService = new IncomeService(incomeRepository);
	
    @Test
    void shouldCreateAndSaveIncomeSuccessfully() {
	    YearMonth expectedMonth = YearMonth.of(2000, 1);
    	Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, expectedMonth);
    	
        Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
        		.thenReturn(expectedIncome);
        
        IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setType(TransactionType.RECURRING);
        incomeRequest.setMonth(expectedMonth.toString());
        
        Income savedIncome = incomeService.createIncome(incomeRequest);

        assertNotNull(savedIncome);
        assertEquals("Salary", savedIncome.getName());
        assertEquals("Company XYZ", savedIncome.getSource());
        assertEquals(2000.0, savedIncome.getAmount());
        assertEquals(TransactionType.RECURRING, savedIncome.getType());
	    assertEquals(expectedMonth, savedIncome.getMonth());

        Mockito.verify(incomeRepository, Mockito.times(1))
        		.saveAndFlush(Mockito.any(Income.class));
    }
    
    @Test
    void shouldReturnListOfIncomesWhenMonthHasIncomes() {
        YearMonth month = YearMonth.of(2000, 1);

        List<Income> incomes = List.of(
            new Income("Salary", "Company XYZ", 3000.0, TransactionType.RECURRING, month),
            new Income("Bonus", "Company ABC", 500.0, TransactionType.ONE_TIME, month)
        );

        try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
            mockedDateUtils.when(() -> DateUtils.getValidYearMonth(month.toString()))
            		.thenReturn(month);
            Mockito.when(incomeRepository.findByMonth(month))
            		.thenReturn(incomes);

            List<Income> result = incomeService.getAllIncomesForMonth(month.toString());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Salary", result.get(0).getName());
            assertEquals("Bonus", result.get(1).getName());

            Mockito.verify(incomeRepository, Mockito.times(1))
                    .findByMonth(month);
        }
    }
    
    @Test
    void shouldReturnIncomeWhenExists() {
	    YearMonth testMonth = YearMonth.of(2000, 1);
    	Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testMonth);
    	
    	Mockito.when(incomeRepository.findById(1L))
    			.thenReturn(Optional.of(expectedIncome));
    	
    	Income retrievedIncome = incomeService.getIncomeById(1L);
    	
    	assertNotNull(retrievedIncome);
        assertEquals("Salary", retrievedIncome.getName());
        assertEquals("Company XYZ", retrievedIncome.getSource());
        assertEquals(2000.0, retrievedIncome.getAmount());
        assertEquals(TransactionType.RECURRING, retrievedIncome.getType());
	    assertEquals(testMonth, retrievedIncome.getMonth());
	    
        Mockito.verify(incomeRepository, Mockito.times(1))
				.findById(1L);
    }
    
    @Test
    void shouldUpdateIncomeWhenExists() {
	    YearMonth testMonth = YearMonth.of(2000, 1);
    	Income existingIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testMonth);

        IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Bonus");
        updateRequest.setSource("Company ABC");
        updateRequest.setAmount(3000.0);
        updateRequest.setType(TransactionType.ONE_TIME);
        updateRequest.setMonth(testMonth.toString());
        
        Mockito.when(incomeRepository.findById(1L))
				.thenReturn(Optional.of(existingIncome));
        Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
        		.thenReturn(existingIncome);

        Income updatedIncome = incomeService.updateIncome(1L, updateRequest);

        assertNotNull(updatedIncome);
        assertEquals("BONUS", updatedIncome.getName());
        assertEquals("COMPANY ABC", updatedIncome.getSource());
        assertEquals(3000.0, updatedIncome.getAmount());
        assertEquals(TransactionType.ONE_TIME, updatedIncome.getType());
        assertEquals(testMonth, updatedIncome.getMonth());
        
        Mockito.verify(incomeRepository, Mockito.times(1))
        		.saveAndFlush(existingIncome);
    }
    
    @Test
    void shouldDeleteIncomeWhenExists() {
        YearMonth testMonth = YearMonth.of(2000, 1);
        Income existingIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testMonth);
        
        Mockito.when(incomeRepository.findById(1L))
                .thenReturn(Optional.of(existingIncome));
        Mockito.doNothing()
                .when(incomeRepository)
                .deleteById(1L);
        
        incomeService.deleteIncome(1L);

        Mockito.verify(incomeRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(incomeRepository, Mockito.times(1))
                .deleteById(1L);
    }
    
    @Test
    void shouldThrowExceptionWhenSaveFails() {
        String errorMessage = "Duplicate Entry";
    	Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
               .thenThrow(new DataIntegrityViolationException(errorMessage));
    	
        IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setType(TransactionType.RECURRING);
        incomeRequest.setMonth(YearMonth.of(2000, 1).toString());
        
        assertThrows(DataIntegrityViolationException.class,
            () -> incomeService.createIncome(incomeRequest),
            errorMessage
        );
    }
    
    @Test
    void shouldThrowExceptionWhenIncomeDoesNotExist() {
    	String errorMessage = "Income not found with id: 99";
        Mockito.when(incomeRepository.findById(99L))
    			.thenReturn(Optional.empty());
        
        IncomeNotFoundException exception = assertThrows(
        		IncomeNotFoundException.class,
        		() -> incomeService.getIncomeById(99L),
        		errorMessage
        );
        
        assertEquals(errorMessage, exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenNoIncomesExistForMonth() {
        YearMonth month = YearMonth.of(2000, 1);
        String errorMessage = "Incomes not found for month: " + month;
        
        try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
            mockedDateUtils.when(() -> DateUtils.getValidYearMonth(month.toString()))
            		.thenReturn(month);
            Mockito.when(incomeRepository.findByMonth(month))
            		.thenThrow(new IncomeNotFoundException(errorMessage));

            assertThrows(IncomeNotFoundException.class,
                () -> incomeService.getAllIncomesForMonth(month.toString()),
                errorMessage
            );
        }
    }
    
    @Test
    void shouldThrowExceptionyWhenUpdatingNonExistentIncome() {
    	String errorMessage = "Income not found with id: 99";

        IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Interest Income");
        updateRequest.setSource("Bank XYZ");
        updateRequest.setAmount(100.0);
        updateRequest.setType(TransactionType.ONE_TIME);
        updateRequest.setMonth(YearMonth.of(2000, 1).toString());
        
        Mockito.when(incomeRepository.findById(99L))
        		.thenReturn(Optional.empty());

        IncomeNotFoundException exception = assertThrows(
        		IncomeNotFoundException.class,
        		() -> incomeService.updateIncome(99L, updateRequest),
        		errorMessage
        );
        
        assertEquals(errorMessage, exception.getMessage());
        Mockito.verify(incomeRepository, Mockito.never())
        		.saveAndFlush(Mockito.any(Income.class));
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentIncome() {
        String errorMessage = "Income not found with id: 99";
        Mockito.when(incomeRepository.findById(99L))
                .thenReturn(Optional.empty());
        
        IncomeNotFoundException exception = assertThrows(
                IncomeNotFoundException.class,
                () -> incomeService.deleteIncome(99L),
                errorMessage
        );
        
        assertEquals(errorMessage, exception.getMessage());
        Mockito.verify(incomeRepository, Mockito.never())
                .deleteById(Mockito.anyLong());
    }
}