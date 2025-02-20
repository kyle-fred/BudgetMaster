package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.model.Budget;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BudgetServiceTest {
	
	private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
	private final BudgetService budgetService = new BudgetService(budgetRepository);
	
	@Test
	void testCalculateBudget() {
		BudgetRequest request = new BudgetRequest();
		request.setIncome(5000.0);
		request.setExpenses(2000.0);
		
		Budget budget = budgetService.calculateandSaveBudget(request);
		
		assertEquals(5000, budget.getIncome());
		assertEquals(2000, budget.getExpenses());
		assertEquals(3000, budget.getSavings());
	}
}