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
	
	public Budget createBudget(BudgetRequest request) {
		Budget budget = new Budget(request.getIncome(), request.getExpenses());
		return budgetRepository.save(budget);
	}
	
	public Optional<Budget> getBudgetById(Long id) {
		return budgetRepository.findById(id);
	}
	
	public Optional<Budget> updateBudget(Long id, BudgetRequest request) {
		Optional<Budget> existingBudget = budgetRepository.findById(id);
		
		if (existingBudget.isPresent()) {
			Budget budget = existingBudget.get();
			budget.setIncome(request.getIncome());
			budget.setExpenses(request.getExpenses());
			budget.setSavings(request.getIncome() - request.getExpenses());
			
			return Optional.of(budgetRepository.save(budget));
		} else {
			return Optional.empty();
		}
	}
}