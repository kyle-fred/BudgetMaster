package com.budgetmaster.service;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.enums.IncomeType;
import com.budgetmaster.repository.IncomeRepository;
import com.budgetmaster.model.Income;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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
        expectedIncome.setIncomeType(IncomeType.RECURRING);
    	
        // Mock successful creation
        Mockito.when(incomeRepository.save(Mockito.any(Income.class)))
        	.thenReturn(expectedIncome);
        
        IncomeRequest incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setIncomeType(IncomeType.RECURRING);
        
        Income savedIncome = incomeService.createIncome(incomeRequest);

        assertNotNull(savedIncome);
        assertEquals(1L, savedIncome.getId());
        assertEquals("Salary", savedIncome.getName());
        assertEquals("Company XYZ", savedIncome.getSource());
        assertEquals(2000.0, savedIncome.getAmount());
        assertEquals(IncomeType.RECURRING, savedIncome.getIncomeType());

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
        incomeRequest.setIncomeType(IncomeType.RECURRING);
        
        assertThrows(DataIntegrityViolationException.class,
            () -> incomeService.createIncome(incomeRequest));
    }
}