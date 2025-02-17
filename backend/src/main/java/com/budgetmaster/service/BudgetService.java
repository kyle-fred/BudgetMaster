package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.model.Budget;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
	public Budget calculateBudget(BudgetRequest request) {
		return new Budget(request.getIncome(), request.getExpenses());
	}
}