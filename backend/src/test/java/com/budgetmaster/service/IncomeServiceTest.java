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

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

class IncomeServiceTest {
	
	private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
	private final IncomeService incomeService = new IncomeService(incomeRepository);
	
    @Test
    void shouldCreateAndSaveIncomeSuccessfully() {
    	
	    YearMonth expectedMonthYear = YearMonth.of(2000, 1);
	    LocalDateTime now = LocalDateTime.now().withNano(0);
        
    	Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, expectedMonthYear);
        expectedIncome.setId(1L);
        expectedIncome.setCreatedAt(now);
        expectedIncome.setLastUpdatedAt(now);
    	
        // Mock successful creation
        Mockito.when(incomeRepository.save(Mockito.any(Income.class)))
        	.thenReturn(expectedIncome);
        
        IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setType(TransactionType.RECURRING);
        incomeRequest.setMonthYear(expectedMonthYear.toString());
        
        Income savedIncome = incomeService.createIncome(incomeRequest);

        assertNotNull(savedIncome);
        assertEquals(1L, savedIncome.getId());
        assertEquals("Salary", savedIncome.getName());
        assertEquals("Company XYZ", savedIncome.getSource());
        assertEquals(2000.0, savedIncome.getAmount());
        assertEquals(TransactionType.RECURRING, savedIncome.getType());
	    assertEquals(expectedMonthYear, savedIncome.getMonthYear());
	    assertNotNull(savedIncome.getCreatedAt());
	    assertNotNull(savedIncome.getLastUpdatedAt());

        Mockito.verify(incomeRepository, Mockito.times(1))
        	.save(Mockito.any(Income.class));
    }
    
	@Test 
	void shouldSetCreatedAtOnSave() {
		
		YearMonth expectedMonthYear = YearMonth.of(2000, 1);
		LocalDateTime now = LocalDateTime.now().withNano(0);
		
		IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setType(TransactionType.RECURRING);
        incomeRequest.setMonthYear(expectedMonthYear.toString());
	    
        Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, expectedMonthYear);
        expectedIncome.setId(1L);
        expectedIncome.setCreatedAt(now);
        expectedIncome.setLastUpdatedAt(now);
	    
	    // Mock successful creation
	    Mockito.when(incomeRepository.save(Mockito.any(Income.class)))
	        .thenReturn(expectedIncome);
	    
	    Income savedIncome = incomeService.createIncome(incomeRequest);
	    
	    assertNotNull(savedIncome.getCreatedAt());
	    assertNotNull(savedIncome.getLastUpdatedAt());
	}
    
    @Test
    void shouldReturnIncomeWhenExists() {
    	
	    YearMonth testYearMonth = YearMonth.of(2000, 1);
	    LocalDateTime now = LocalDateTime.now().withNano(0);
    	
    	Income expectedIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
    	expectedIncome.setId(1L);
    	expectedIncome.setCreatedAt(now);
    	expectedIncome.setLastUpdatedAt(now);
    	
    	// Mock successful read
    	Mockito.when(incomeRepository.findById(1L)).thenReturn(Optional.of(expectedIncome));
    	
    	Optional<Income> retrievedIncome = incomeService.getIncomeById(1L);
    	
    	assertTrue(retrievedIncome.isPresent());
    	assertEquals(1L, retrievedIncome.get().getId());    	
        assertEquals("Salary", retrievedIncome.get().getName());
        assertEquals("Company XYZ", retrievedIncome.get().getSource());
        assertEquals(2000.0, retrievedIncome.get().getAmount());
        assertEquals(TransactionType.RECURRING, retrievedIncome.get().getType());
	    assertEquals(testYearMonth, retrievedIncome.get().getMonthYear());
	    assertNotNull(retrievedIncome.get().getCreatedAt());
	    assertNotNull(retrievedIncome.get().getLastUpdatedAt());
    }
    
    @Test
    void shouldUpdateIncomeWhenExists() {
    	
	    YearMonth testYearMonth = YearMonth.of(2000, 1);
	    LocalDateTime now = LocalDateTime.now().withNano(0);
    	
    	Income existingIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
        existingIncome.setId(1L);
        existingIncome.setCreatedAt(now);
        existingIncome.setLastUpdatedAt(now);

        IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Bonus");
        updateRequest.setSource("Company ABC");
        updateRequest.setAmount(3000.0);
        updateRequest.setType(TransactionType.ONE_TIME);

        // Mock successful update
        Mockito.when(incomeRepository.findById(1L)).thenReturn(Optional.of(existingIncome));
        Mockito.when(incomeRepository.save(Mockito.any(Income.class))).thenReturn(existingIncome);

        Optional<Income> updatedIncome = incomeService.updateIncome(1L, updateRequest);

        assertTrue(updatedIncome.isPresent());
        assertEquals(1L, updatedIncome.get().getId());
        assertEquals("Bonus", updatedIncome.get().getName());
        assertEquals("Company ABC", updatedIncome.get().getSource());
        assertEquals(3000.0, updatedIncome.get().getAmount());
        assertEquals(TransactionType.ONE_TIME, updatedIncome.get().getType());
        
        Mockito.verify(incomeRepository, Mockito.times(1)).save(existingIncome);
    }
    
    @Test
    void shouldUpdateLastUpdatedAtOnModification() {
        
    	YearMonth testYearMonth = YearMonth.of(2000, 1);
        LocalDateTime createdAt = LocalDateTime.now().withNano(0).minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now().withNano(0);

    	Income existingIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
        existingIncome.setId(1L);
        existingIncome.setCreatedAt(createdAt);
        existingIncome.setLastUpdatedAt(createdAt);

        IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Bonus");
        updateRequest.setSource("Company ABC");
        updateRequest.setAmount(3000.0);
        updateRequest.setType(TransactionType.ONE_TIME);
        
    	Income updatedIncome = new Income("Bonus", "Company ABC", 3000.0, TransactionType.ONE_TIME, testYearMonth);
    	updatedIncome.setId(1L);
    	updatedIncome.setCreatedAt(createdAt);
    	updatedIncome.setLastUpdatedAt(updatedAt);

        Mockito.when(incomeRepository.findById(1L)).thenReturn(Optional.of(existingIncome));
        Mockito.when(incomeRepository.save(Mockito.any(Income.class))).thenReturn(updatedIncome);

        Optional<Income> result = incomeService.updateIncome(1L, updateRequest);

        assertTrue(result.isPresent());
        assertEquals(updatedAt, result.get().getLastUpdatedAt());
        assertEquals(createdAt, result.get().getCreatedAt());
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
    void shouldThrowExceptionWhenSaveFails() {
    	
    	// Mock unsuccessful creation
        Mockito.when(incomeRepository.save(Mockito.any(Income.class)))
               .thenThrow(new DataIntegrityViolationException("Duplicate Entry"));

        IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setType(TransactionType.RECURRING);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> incomeService.createIncome(incomeRequest));
    }
    
    @Test
    void shouldReturnEmptyWhenIncomeDoesNotExist() {
    	
    	// Mock unsuccessful read
    	Mockito.when(incomeRepository.findById(99L)).thenReturn(Optional.empty());
        
        Optional<Income> retrievedIncome = incomeService.getIncomeById(99L);
        
        assertFalse(retrievedIncome.isPresent());
    }
    
    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentIncome() {
        
    	IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Interest Income");
        updateRequest.setSource("Bank XYZ");
        updateRequest.setAmount(100.0);
        updateRequest.setType(TransactionType.ONE_TIME);

        // Mock unsuccessful update
        Mockito.when(incomeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Income> updatedIncome = incomeService.updateIncome(99L, updateRequest);

        assertFalse(updatedIncome.isPresent());
        Mockito.verify(incomeRepository, Mockito.never()).save(Mockito.any(Income.class));
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