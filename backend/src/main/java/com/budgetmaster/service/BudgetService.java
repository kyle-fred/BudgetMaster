package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.model.Budget;
import com.budgetmaster.repository.BudgetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
	
	@Autowired
	private BudgetRepository budgetRepository;
	
	public Budget calculateandSaveBudget(BudgetRequest request) {
		Budget budget = new Budget(request.getIncome(), request.getExpenses());
		return budgetRepository.save(budget);
	}
}