package com.budgetmaster.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.budgetmaster.model.Budget;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BudgetRepositoryTest {
	
	@Autowired
	private BudgetRepository budgetRepository;
	
	@Autowired
	private IncomeRepository incomeRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Test
	public void testCreatedAtAndLastUpdatedAtOnInsert() {
		Budget budget = new Budget(3000.0, 1500.0, YearMonth.of(2000, 1));
		budgetRepository.save(budget);
		
		entityManager.flush();
		entityManager.refresh(budget);
		
		Optional<Budget> foundBudget = budgetRepository.findById(budget.getId());
		
		assertTrue(foundBudget.isPresent());
		assertNotNull(foundBudget.get().getCreatedAt());
		assertNotNull(foundBudget.get().getLastUpdatedAt());
	}
}