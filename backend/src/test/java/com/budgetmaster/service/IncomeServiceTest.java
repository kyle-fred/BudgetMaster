package com.budgetmaster.service;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.repository.IncomeRepository;
import com.budgetmaster.model.Income;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Optional;

class IncomeServiceTest {
	
	private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
	private final IncomeService incomeService = new IncomeService(incomeRepository);
	
    @Test
    void shouldCreateAndSaveIncomeToDbSuccessfully() {
        
    	Income expectedIncome = new Income();
        expectedIncome.setId(1L);
        expectedIncome.setName("Salary");
        expectedIncome.setSource("Company XYZ");
        expectedIncome.setAmount(2000.0);
        expectedIncome.setTransactionType(TransactionType.RECURRING);
    	
        // Mock successful creation
        Mockito.when(incomeRepository.save(Mockito.any(Income.class)))
        	.thenReturn(expectedIncome);
        
        IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setTransactionType(TransactionType.RECURRING);
        
        Income savedIncome = incomeService.createIncome(incomeRequest);

        assertNotNull(savedIncome);
        assertEquals(1L, savedIncome.getId());
        assertEquals("Salary", savedIncome.getName());
        assertEquals("Company XYZ", savedIncome.getSource());
        assertEquals(2000.0, savedIncome.getAmount());
        assertEquals(TransactionType.RECURRING, savedIncome.getTransactionType());

        Mockito.verify(incomeRepository, Mockito.times(1))
        	.save(Mockito.any(Income.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaveFails() {
    	// Mock unsuccessful creation
        Mockito.when(incomeRepository.save(Mockito.any(Income.class)))
               .thenThrow(new DataIntegrityViolationException("Duplicate Entry"));

        IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setTransactionType(TransactionType.RECURRING);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> incomeService.createIncome(incomeRequest));
    }
    
    @Test
    void shouldReturnIncomeWhenExists() {
    	Income income = new Income();
    	income.setId(1L);
    	income.setName("Salary");
    	income.setSource("Company XYZ");
    	income.setAmount(2000.0);
    	income.setTransactionType(TransactionType.RECURRING);
    	
    	// Mock successful read
    	Mockito.when(incomeRepository.findById(1L)).thenReturn(Optional.of(income));
    	
    	Optional<Income> retrievedIncome = incomeService.getIncomeById(1L);
    	
    	assertTrue(retrievedIncome.isPresent());
    	assertEquals(1L, retrievedIncome.get().getId());    	
        assertEquals("Salary", retrievedIncome.get().getName());
        assertEquals("Company XYZ", retrievedIncome.get().getSource());
        assertEquals(2000.0, retrievedIncome.get().getAmount());
        assertEquals(TransactionType.RECURRING, retrievedIncome.get().getTransactionType());
    }
    
    @Test
    void shouldReturnEmptyWhenIncomeDoesNotExist() {
    	// Mock unsuccessful read
    	Mockito.when(incomeRepository.findById(99L)).thenReturn(Optional.empty());
        
        Optional<Income> retrievedIncome = incomeService.getIncomeById(99L);
        
        assertFalse(retrievedIncome.isPresent());
    }
    
    @Test
    void shouldUpdateIncomeWhenExists() {
        Income existingIncome = new Income();
        existingIncome.setId(1L);
        existingIncome.setName("Salary");
        existingIncome.setSource("Company XYZ");
        existingIncome.setAmount(2000.0);
        existingIncome.setTransactionType(TransactionType.RECURRING);

        IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Interest Income");
        updateRequest.setSource("Bank XYZ");
        updateRequest.setAmount(100.0);
        updateRequest.setTransactionType(TransactionType.ONE_TIME);

        // Mock successful update
        Mockito.when(incomeRepository.findById(1L)).thenReturn(Optional.of(existingIncome));
        Mockito.when(incomeRepository.save(Mockito.any(Income.class))).thenReturn(existingIncome);

        Optional<Income> updatedIncome = incomeService.updateIncome(1L, updateRequest);

        assertTrue(updatedIncome.isPresent());
        assertEquals(1L, updatedIncome.get().getId());
        assertEquals("Interest Income", updatedIncome.get().getName());
        assertEquals("Bank XYZ", updatedIncome.get().getSource());
        assertEquals(100.0, updatedIncome.get().getAmount());
        assertEquals(TransactionType.ONE_TIME, updatedIncome.get().getTransactionType());
        
        Mockito.verify(incomeRepository, Mockito.times(1)).save(existingIncome);
    }
    
    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentIncome() {
        IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Interest Income");
        updateRequest.setSource("Bank XYZ");
        updateRequest.setAmount(100.0);
        updateRequest.setTransactionType(TransactionType.ONE_TIME);

        // Mock unsuccessful update
        Mockito.when(incomeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Income> updatedIncome = incomeService.updateIncome(99L, updateRequest);

        assertFalse(updatedIncome.isPresent());
        Mockito.verify(incomeRepository, Mockito.never()).save(Mockito.any(Income.class));
    }
    
    @Test
    void shouldDeleteIncomeWhenExists() {
    	// Mock successful delete
    	Mockito.when(incomeRepository.existsById(1L)).thenReturn(true);
    	Mockito.doNothing().when(incomeRepository).deleteById(1L);

        boolean deleted = incomeService.deleteIncome(1L);

        assertTrue(deleted);
        Mockito.verify(incomeRepository, Mockito.times(1)).deleteById(1L);
    }
    
    @Test
    void shouldReturnFalseWhenDeletingNonExistentIncome() {
    	// Mock unsuccessful delete
    	Mockito.when(incomeRepository.existsById(99L)).thenReturn(false);

        boolean deleted = incomeService.deleteIncome(99L);

        assertFalse(deleted);
        Mockito.verify(incomeRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}