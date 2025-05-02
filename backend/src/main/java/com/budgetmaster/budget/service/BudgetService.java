package com.budgetmaster.budget.service;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.common.constants.error.ErrorMessages;
import com.budgetmaster.common.service.EntityLookupService;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.income.model.Income;

import java.time.YearMonth;
import java.util.Currency;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService extends EntityLookupService {
	
	private final BudgetRepository budgetRepository;
	
	public BudgetService(BudgetRepository budgetRepository) {
		this.budgetRepository = budgetRepository;
	}
	
	public Budget getBudgetByMonth(String monthString) {
		YearMonth month = DateUtils.getValidYearMonth(monthString);
		return findByCustomFinderOrThrow(
				budgetRepository::findByMonth,
				month,
				createMonthNotFoundException(month)
		);
	}
	
	public Budget getBudgetById(Long id) {
		return findByIdOrThrow(
				budgetRepository,
				id,
				createIdNotFoundException(id)
		);
	}
	
	@Transactional
	public void deleteBudget(Long id) {
		getBudgetById(id);
		budgetRepository.deleteById(id);
	}

	public void updateBudgetWithIncome(Income income) {
		Budget budget = getOrInitializeBudget(income.getMonth(), income.getMoney().getCurrency());
		budget.addIncome(income.getMoney().getAmount());
		budgetRepository.save(budget);
	}

	/**
	 * Returns a budget for the given month. If no budget exists, a new one is created.
	 */
	private Budget getOrInitializeBudget(YearMonth month, Currency currency) {
		return budgetRepository.findByMonth(month)
			.orElseGet(() -> new Budget(month, currency));
	}
	
	/**
	 * Creates a supplier for BudgetNotFoundException when entity is not found by ID.
	 */
	private Supplier<BudgetNotFoundException> createIdNotFoundException(Long id) {
		return () -> new BudgetNotFoundException(String.format(ErrorMessages.Budget.NOT_FOUND_BY_ID, id));
	}
	
	/**
	 * Creates a supplier for BudgetNotFoundException when entity is not found by month.
	 */
	private Supplier<BudgetNotFoundException> createMonthNotFoundException(YearMonth month) {
		return () -> new BudgetNotFoundException(String.format(ErrorMessages.Budget.NOT_FOUND_BY_MONTH, month));
	}
}