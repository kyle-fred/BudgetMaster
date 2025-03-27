package com.budgetmaster.repository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
    	
    	YearMonth testYearMonth = YearMonth.now();
    	
        Budget initialBudget = new Budget(5000.0, 3000.0, testYearMonth);
        Budget persistedBudget = budgetRepository.save(initialBudget);

        assertNotNull(persistedBudget.getCreatedAt());
        assertNotNull(persistedBudget.getLastUpdatedAt());
        assertEquals(persistedBudget.getCreatedAt(), persistedBudget.getLastUpdatedAt());
    }

    @Test
    @Transactional
    public void testBudgetUpdateTimestampOnModification() throws InterruptedException {
    	
    	YearMonth testYearMonth = YearMonth.now();
    	
        Budget initialBudget = new Budget(5000.0, 3000.0, testYearMonth);
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
    
    @Test
    @Transactional
    public void testFindByMonthYear() {
        YearMonth testYearMonth = YearMonth.of(2000, 1);
        Budget budget = new Budget(6000.0, 2500.0, testYearMonth);
        budgetRepository.save(budget);
        
        Optional<Budget> foundBudget = budgetRepository.findByMonthYear(testYearMonth);
        
        assertTrue(foundBudget.isPresent());
        assertEquals(6000.0, foundBudget.get().getIncome());
        assertEquals(2500.0, foundBudget.get().getExpenses());
        assertEquals(testYearMonth, foundBudget.get().getMonthYear());
    }

    @Test
    @Transactional
    public void testDeleteByMonthYear() {
        YearMonth testYearMonth = YearMonth.of(2000, 1);
        Budget budget = new Budget(7000.0, 3000.0, testYearMonth);
        budgetRepository.save(budget);
        
        assertTrue(budgetRepository.findByMonthYear(testYearMonth).isPresent());
        
        budgetRepository.deleteByMonthYear(testYearMonth);
        
        assertFalse(budgetRepository.findByMonthYear(testYearMonth).isPresent());
    }
    
    @Test
    void shouldNotAllowDuplicateMonthYear() {
    	
        YearMonth monthYear = YearMonth.of(2000, 1);
        Budget budget = new Budget(5000.0, 2000.0, monthYear);
        budgetRepository.save(budget);

        Budget duplicateMonthBudget = new Budget(6000.0, 2500.0, monthYear);

        assertThrows(DataIntegrityViolationException.class, () -> {
            budgetRepository.saveAndFlush(duplicateMonthBudget);
        });
    }
}
