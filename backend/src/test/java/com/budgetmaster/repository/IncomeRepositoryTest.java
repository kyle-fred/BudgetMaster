package com.budgetmaster.repository;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.model.Income;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

    @Test
    @Transactional
    public void testIncomeCreationTimestampOnSave() {
    	
    	YearMonth testYearMonth = YearMonth.now();
    	
        Income initialIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
        Income persistedIncome = incomeRepository.save(initialIncome);

        assertNotNull(persistedIncome.getCreatedAt());
        assertNotNull(persistedIncome.getLastUpdatedAt());
        assertEquals(persistedIncome.getCreatedAt(), persistedIncome.getLastUpdatedAt());
    }

    @Test
    @Transactional
    public void testIncomeUpdateTimestampOnModification() throws InterruptedException {
    	
    	YearMonth testYearMonth = YearMonth.now();
    	
    	Income initialIncome = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testYearMonth);
        Income persistedIncome = incomeRepository.save(initialIncome);

        LocalDateTime initialLastUpdatedAt = persistedIncome.getLastUpdatedAt();
        incomeRepository.flush();
        
        Thread.sleep(1000);
        
        persistedIncome.setName("Bonus");
        persistedIncome.setSource("Company ABC");
        persistedIncome.setAmount(3000.0);
        persistedIncome.setType(TransactionType.ONE_TIME);
        
        incomeRepository.save(persistedIncome);
        
        // Write changes to the database immediately
        incomeRepository.flush();
        
        Income updatedIncome = incomeRepository.findById(persistedIncome.getId()).orElseThrow();

        assertNotEquals(initialLastUpdatedAt, updatedIncome.getLastUpdatedAt());
    }
}
