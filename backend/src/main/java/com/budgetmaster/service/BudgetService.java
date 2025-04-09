package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.utils.model.FinancialModelUtils;
import com.budgetmaster.model.Budget;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {
	
	private final BudgetRepository budgetRepository;
	
	public BudgetService(BudgetRepository budgetRepository) {
		this.budgetRepository = budgetRepository;
	}
	
	public Budget createBudget(BudgetRequest request) {
		YearMonth monthYear = DateUtils.getValidYearMonth(request.getMonthYear());
		Optional<Budget> existingMonthlyBudget = budgetRepository.findByMonthYear(monthYear);
		
		if (existingMonthlyBudget.isPresent()) {
			return existingMonthlyBudget.get();
		}
		
		Budget newMonthlyBudget = FinancialModelUtils.buildBudget(request);
		return budgetRepository.saveAndFlush(newMonthlyBudget);
	}
	
	public Budget getBudgetByMonthYear(String monthYearString) {
		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
		return findBudgetOrThrow(monthYear);
	}
	
	public Budget getBudgetById(Long id) {
		return findBudgetOrThrow(id);
	}
	
	public Budget updateBudget(Long id, BudgetRequest request) {
		Budget budget = findBudgetOrThrow(id);
		FinancialModelUtils.modifyBudget(budget, request);
		return budgetRepository.saveAndFlush(budget);
	}
	
	@Transactional
	public void deleteBudget(Long id) {
		findBudgetOrThrow(id);
		budgetRepository.deleteById(id);
	}
	
	private Budget findBudgetOrThrow(YearMonth monthYear) {
		Optional<Budget> budgetOptional = budgetRepository.findByMonthYear(monthYear);
		if (budgetOptional.isEmpty()) {
			throw new BudgetNotFoundException("Budget not found for month: " + monthYear);
		}		
		return budgetOptional.get();
	}
	
	private Budget findBudgetOrThrow(Long id) {
		Optional<Budget> budgetOptional = budgetRepository.findById(id);		
		if (budgetOptional.isEmpty()) {
			throw new BudgetNotFoundException("Budget not found with id: " + id);
		}
		return budgetOptional.get();
	}
}