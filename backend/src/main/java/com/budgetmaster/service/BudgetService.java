package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.model.Budget;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class BudgetService {
	
	private final BudgetRepository budgetRepository;
	
	public BudgetService(BudgetRepository budgetRepository) {
		this.budgetRepository = budgetRepository;
	}
	
	public Optional<Budget> getBudgetById(Long id) {
		return budgetRepository.findById(id);
	}
	
	public Budget calculateAndSaveBudget(BudgetRequest request) {
		Budget budget = new Budget(request.getIncome(), request.getExpenses());
		return budgetRepository.save(budget);
	}
}