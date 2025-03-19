package com.budgetmaster.service;

import com.budgetmaster.repository.BudgetRepository;
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
	
	public Optional<Budget> getBudgetByMonthYear(YearMonth monthYear) {
		return budgetRepository.findByMonthYear(monthYear);
	}
	
	@Transactional
	public boolean deleteBudgetByMonthYear(YearMonth monthYear) {
        Optional<Budget> budget = budgetRepository.findByMonthYear(monthYear);
        if (budget.isPresent()) {
            budgetRepository.delete(budget.get());
            return true;
        } else {
            return false;
        }
	}
}