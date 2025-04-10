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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.budgetmaster.exception.IncomeNotFoundException;

class IncomeServiceTest {
	
	private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
	private final IncomeService incomeService = new IncomeService(incomeRepository);
	
    @Test
    void shouldCreateAndSaveIncomeSuccessfully() {
	    YearMonth expectedMonthYear = YearMonth.of(2000, 1);
    	Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, expectedMonthYear);
    	
        Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
        		.thenReturn(expectedIncome);
        
        IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setType(TransactionType.RECURRING);
        incomeRequest.setMonthYear("2000-01");
        
        Income savedIncome = incomeService.createIncome(incomeRequest);

        assertNotNull(savedIncome);
        assertEquals("Salary", savedIncome.getName());
        assertEquals("Company XYZ", savedIncome.getSource());
        assertEquals(2000.0, savedIncome.getAmount());
        assertEquals(TransactionType.RECURRING, savedIncome.getType());
	    assertEquals(expectedMonthYear, savedIncome.getMonthYear());

        Mockito.verify(incomeRepository, Mockito.times(1))
        		.saveAndFlush(Mockito.any(Income.class));
    }
    
    @Test
    void shouldReturnListOfIncomesWhenMonthHasIncomes() {
        YearMonth monthYear = YearMonth.of(2000, 1);

        List<Income> incomes = List.of(
            new Income("Salary", "Company XYZ", 3000.0, TransactionType.RECURRING, monthYear),
            new Income("Bonus", "Company ABC", 500.0, TransactionType.ONE_TIME, monthYear)
        );

        try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
            mockedDateUtils.when(() -> DateUtils.getValidYearMonth("2000-01"))
            		.thenReturn(monthYear);
            Mockito.when(incomeRepository.findByMonthYear(monthYear))
            		.thenReturn(incomes);

            List<Income> result = incomeService.getAllIncomesForMonth("2000-01");

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Salary", result.get(0).getName());
            assertEquals("Bonus", result.get(1).getName());

            Mockito.verify(incomeRepository, Mockito.times(1)).findByMonthYear(monthYear);
        }
    }
    
    @Test
    void shouldReturnIncomeWhenExists() {
	    YearMonth testYearMonth = YearMonth.of(2000, 1);
    	Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
    	
    	Mockito.when(incomeRepository.findById(1L))
    			.thenReturn(Optional.of(expectedIncome));
    	
    	Income retrievedIncome = incomeService.getIncomeById(1L);
    	
    	assertNotNull(retrievedIncome);
        assertEquals("Salary", retrievedIncome.getName());
        assertEquals("Company XYZ", retrievedIncome.getSource());
        assertEquals(2000.0, retrievedIncome.getAmount());
        assertEquals(TransactionType.RECURRING, retrievedIncome.getType());
	    assertEquals(testYearMonth, retrievedIncome.getMonthYear());
	    
        Mockito.verify(incomeRepository, Mockito.times(1))
				.findById(1L);
    }
    
    @Test
    void shouldUpdateIncomeWhenExists() {
	    YearMonth testYearMonth = YearMonth.of(2000, 1);
    	Income existingIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);

        IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Bonus");
        updateRequest.setSource("Company ABC");
        updateRequest.setAmount(3000.0);
        updateRequest.setType(TransactionType.ONE_TIME);
        updateRequest.setMonthYear("2000-01");
        
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
        assertEquals(testYearMonth, updatedIncome.getMonthYear());
        
        Mockito.verify(incomeRepository, Mockito.times(1))
        		.saveAndFlush(existingIncome);
    }
    
    @Test
    void shouldDeleteIncomeWhenExists() {
        YearMonth testYearMonth = YearMonth.of(2000, 1);
        Income existingIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
        
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
    	Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
               .thenThrow(new DataIntegrityViolationException("Duplicate Entry"));
    	
        IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setType(TransactionType.RECURRING);
        incomeRequest.setMonthYear("2000-01");
        
        assertThrows(DataIntegrityViolationException.class,
            () -> incomeService.createIncome(incomeRequest));
    }
    
    @Test
    void shouldReturnEmptyWhenIncomeDoesNotExist() {
    	Mockito.when(incomeRepository.findById(99L))
    			.thenReturn(Optional.empty());
        
        IncomeNotFoundException exception = assertThrows(
        		IncomeNotFoundException.class,
        		() -> incomeService.getIncomeById(99L)
        );
        
        assertEquals("Income not found with id: 99", exception.getMessage());
    }
    
    @Test
    void shouldReturnEmptyListWhenNoIncomesExistForMonth() {
        YearMonth monthYear = YearMonth.of(2000, 1);
        
        try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
            mockedDateUtils.when(() -> DateUtils.getValidYearMonth("2000-01"))
            		.thenReturn(monthYear);
            Mockito.when(incomeRepository.findByMonthYear(monthYear))
            		.thenReturn(Collections.emptyList());
            
            List<Income> result = incomeService.getAllIncomesForMonth("2000-01");

            assertNotNull(result);
            assertTrue(result.isEmpty());

            Mockito.verify(incomeRepository, Mockito.times(1)).findByMonthYear(monthYear);
        }
    }
    
    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentIncome() {
        IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Interest Income");
        updateRequest.setSource("Bank XYZ");
        updateRequest.setAmount(100.0);
        updateRequest.setType(TransactionType.ONE_TIME);
        updateRequest.setMonthYear("2000-01");
        
        Mockito.when(incomeRepository.findById(99L))
        		.thenReturn(Optional.empty());

        IncomeNotFoundException exception = assertThrows(
        		IncomeNotFoundException.class,
        		() -> incomeService.updateIncome(99L, updateRequest)
        );
        
        assertEquals("Income not found with id: 99", exception.getMessage());
        Mockito.verify(incomeRepository, Mockito.never())
        		.saveAndFlush(Mockito.any(Income.class));
    }
    
    @Test
    void shouldReturnFalseWhenDeletingNonExistentIncome() {
        Mockito.when(incomeRepository.findById(99L))
                .thenReturn(Optional.empty());
        
        IncomeNotFoundException exception = assertThrows(
                IncomeNotFoundException.class,
                () -> incomeService.deleteIncome(99L)
        );
        
        assertEquals("Income not found with id: 99", exception.getMessage());
        Mockito.verify(incomeRepository, Mockito.never())
                .deleteById(Mockito.anyLong());
    }
}