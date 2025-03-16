package com.budgetmaster.repository;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.model.Expense;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    @Transactional
    public void testExpenseCreationTimestampOnSave() {
    	
    	YearMonth testYearMonth = YearMonth.now();
    	
        Expense initialExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testYearMonth);
        Expense persistedExpense = expenseRepository.save(initialExpense);

        assertNotNull(persistedExpense.getCreatedAt());
        assertNotNull(persistedExpense.getLastUpdatedAt());
        assertEquals(persistedExpense.getCreatedAt(), persistedExpense.getLastUpdatedAt());
    }
    
    @Test
    @Transactional
    public void testExpenseUpdateTimestampOnModification() throws InterruptedException {
    	
    	YearMonth testYearMonth = YearMonth.now();
    	
    	Expense initialExpense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testYearMonth);
        Expense persistedExpense = expenseRepository.save(initialExpense);

        LocalDateTime initialLastUpdatedAt = persistedExpense.getLastUpdatedAt();
        expenseRepository.flush();
        
        Thread.sleep(1000);
        
        persistedExpense.setName("Gas Bill");
        persistedExpense.setAmount(100.0);
        persistedExpense.setCategory(ExpenseCategory.UTILITIES);
        persistedExpense.setType(TransactionType.ONE_TIME);
        
        expenseRepository.save(persistedExpense);
        
        // Write changes to the database immediately
        expenseRepository.flush();
        
        Expense updatedExpense = expenseRepository.findById(persistedExpense.getId()).orElseThrow();

        assertNotEquals(initialLastUpdatedAt, updatedExpense.getLastUpdatedAt());
    }
}
