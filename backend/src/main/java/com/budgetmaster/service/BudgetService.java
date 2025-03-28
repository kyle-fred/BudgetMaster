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
		Optional<Budget> existingBudget = budgetRepository.findByMonthYear(monthYear);
		
		if (existingBudget.isPresent()) {
			return existingBudget.get();
		}
		
		Budget budget = FinancialModelUtils.buildBudget(request);
		return budgetRepository.saveAndFlush(budget);
	}
	
	public Budget getBudgetByMonthYear(String monthYearString) {
		return findBudgetOrThrow(monthYearString);
	}
	
	public Budget updateBudget(String monthYearString, BudgetRequest request) {
		Budget budget = findBudgetOrThrow(monthYearString);
		FinancialModelUtils.modifyBudget(budget, request);
		
		return budgetRepository.saveAndFlush(budget);
	}
	
	@Transactional
	public void deleteBudget(String monthYearString) {
		Budget budget = findBudgetOrThrow(monthYearString);
		budgetRepository.deleteByMonthYear(budget.getMonthYear());
	}
	
	private Budget findBudgetOrThrow(String monthYearString) {
		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
		Optional<Budget> budgetOptional = budgetRepository.findByMonthYear(monthYear);
		
		if (budgetOptional.isEmpty()) {
			throw new BudgetNotFoundException("Budget not found for month: " + monthYear);
		}
		return budgetOptional.get();
	}
}