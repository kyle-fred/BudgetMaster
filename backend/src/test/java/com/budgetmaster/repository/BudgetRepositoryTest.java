package com.budgetmaster.repository;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.budgetmaster.model.Budget;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class BudgetRepositoryTest {

    @Autowired
    private BudgetRepository budgetRepository;

    @Test
    @Transactional
    public void testBudgetCreationTimestampOnSave() {
    	
        Budget initialBudget = new Budget();
        initialBudget.setIncome(5000.0);
        initialBudget.setExpenses(3000.0);
        initialBudget.setSavings(initialBudget.getIncome() - initialBudget.getExpenses());
        initialBudget.setMonthYear(YearMonth.now());
        Budget persistedBudget = budgetRepository.save(initialBudget);

        assertNotNull(persistedBudget.getCreatedAt());
        assertNotNull(persistedBudget.getLastUpdatedAt());
        assertEquals(persistedBudget.getCreatedAt(), persistedBudget.getLastUpdatedAt());
    }

    @Test
    @Transactional
    public void testBudgetUpdateTimestampOnModification() throws InterruptedException {
    	
        Budget initialBudget = new Budget();
        initialBudget.setIncome(5000.0);
        initialBudget.setExpenses(3000.0);
        initialBudget.setSavings(initialBudget.getIncome() - initialBudget.getExpenses());
        initialBudget.setMonthYear(YearMonth.now());
        Budget persistedBudget = budgetRepository.save(initialBudget);

        LocalDateTime initialLastUpdatedAt = persistedBudget.getLastUpdatedAt();
        budgetRepository.flush();
        
        Thread.sleep(1000);
        
        persistedBudget.setIncome(4000.0);
        persistedBudget.setExpenses(2000.0);
        persistedBudget.setSavings(persistedBudget.getIncome() - persistedBudget.getExpenses());
        
        budgetRepository.save(persistedBudget);
        
        // Write changes to the database immediately
        budgetRepository.flush();
        
        Budget updatedBudget = budgetRepository.findById(persistedBudget.getId()).orElseThrow();

        assertNotEquals(initialLastUpdatedAt, updatedBudget.getLastUpdatedAt());
    }
}
