package com.budgetmaster.service;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.repository.BudgetRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.utils.model.FinancialModelUtils;
import com.budgetmaster.utils.service.ServiceUtils;
import com.budgetmaster.model.Budget;

import java.time.YearMonth;
import java.util.Optional;
import java.util.function.Supplier;

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
		return ServiceUtils.findByCustomFinderOrThrow(
				budgetRepository::findByMonthYear,
				monthYear,
				createMonthYearNotFoundException(monthYear)
		);
	}
	
	public Budget getBudgetById(Long id) {
		return ServiceUtils.findByIdOrThrow(budgetRepository,
				id,
				createIdNotFoundException(id)
		);
	}
	
	public Budget updateBudget(Long id, BudgetRequest request) {
		Budget budget = getBudgetById(id);
		FinancialModelUtils.modifyBudget(budget, request);
		return budgetRepository.saveAndFlush(budget);
	}
	
	@Transactional
	public void deleteBudget(Long id) {
		getBudgetById(id);
		budgetRepository.deleteById(id);
	}
	
	/**
	 * Creates a supplier for BudgetNotFoundException when entity is not found by ID.
	 */
	private Supplier<BudgetNotFoundException> createIdNotFoundException(Long id) {
		return () -> new BudgetNotFoundException("Budget not found for id: " + id);
	}
	
	/**
	 * Creates a supplier for BudgetNotFoundException when entity is not found by month/year.
	 */
	private Supplier<BudgetNotFoundException> createMonthYearNotFoundException(YearMonth monthYear) {
		return () -> new BudgetNotFoundException("Budget not found for month: " + monthYear);
	}
}