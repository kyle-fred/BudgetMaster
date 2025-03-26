package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
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
		Budget budget = FinancialModelUtils.buildBudget(request);
		return budgetRepository.save(budget);
	}
	
	public Optional<Budget> getBudgetByMonthYear(String monthYearString) {
		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
		return budgetRepository.findByMonthYear(monthYear);
	}
	
	public Optional<Budget> updateBudget(String monthYearString, BudgetRequest request) {
		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
		Optional<Budget> existingBudget = budgetRepository.findByMonthYear(monthYear);
		
		if (existingBudget.isPresent()) {
			Budget budget = existingBudget.get();
			FinancialModelUtils.modifyBudget(budget, request);
			
			return Optional.of(budgetRepository.save(budget));
		} else {
			return Optional.empty();
		}
	}
	
	@Transactional
	public boolean deleteBudget(String monthYearString) {
		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
		Optional<Budget> budget = budgetRepository.findByMonthYear(monthYear);
		
		if (budget.isPresent()) {
			budgetRepository.deleteByMonthYear(monthYear);
			return true;
		}
		return false;
	}
}