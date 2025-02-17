package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.model.Budget;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BudgetServiceTest {
	
	private final BudgetService budgetService = new BudgetService();
	
	@Test
	void testCalculateBudget() {
		BudgetRequest request = new BudgetRequest();
		request.setIncome(5000);
		request.setExpenses(2000);
		
		Budget budget = budgetService.calculateBudget(request);
		
		assertEquals(5000, budget.getIncome());
		assertEquals(2000, budget.getExpenses());
		assertEquals(3000, budget.getSavings());
	}
}