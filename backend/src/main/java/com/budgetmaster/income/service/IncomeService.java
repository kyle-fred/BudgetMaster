package com.budgetmaster.income.service;

import com.budgetmaster.budget.service.logic.ApplyIncomeToBudget;
import com.budgetmaster.common.constants.error.ErrorMessages;
import com.budgetmaster.common.service.EntityLookupService;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.income.repository.IncomeRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomeService extends EntityLookupService {
	
	private final IncomeRepository incomeRepository;
	private final ApplyIncomeToBudget applyIncomeToBudget;
	
	public IncomeService(IncomeRepository incomeRepository, ApplyIncomeToBudget applyIncomeToBudget) {
		this.incomeRepository = incomeRepository;
		this.applyIncomeToBudget = applyIncomeToBudget;
	}
	
	@Transactional
	public Income createIncome(IncomeRequest request) {
		Income income = incomeRepository.saveAndFlush(Income.from(request));
		applyIncomeToBudget.apply(income);
		return income;
	}
	
	public List<Income> getAllIncomesForMonth(String monthString) {
		YearMonth month = DateUtils.getValidYearMonth(monthString);
		return findListByCustomFinderOrThrow(
			incomeRepository::findByMonth,
			month,
			createMonthNotFoundException(month)
		);
	}
	
	public Income getIncomeById(Long id) {
		return findByIdOrThrow(
				incomeRepository,
				id,
				createIdNotFoundException(id)
		);
	}
	
	public Income updateIncome(Long id, IncomeRequest request) {
		Income income = getIncomeById(id);
		income.updateFrom(request);
		return incomeRepository.saveAndFlush(income);
	}
	
	@Transactional
	public void deleteIncome(Long id) {
		getIncomeById(id);
		incomeRepository.deleteById(id);
	}
	
	/**
	 * Creates a supplier for IncomeNotFoundException when entity is not found by ID.
	 */
	private Supplier<IncomeNotFoundException> createIdNotFoundException(Long id) {
		return () -> new IncomeNotFoundException(String.format(ErrorMessages.Income.NOT_FOUND_BY_ID, id));
	}
	
	/**
	 * Creates a supplier for IncomeNotFoundException when no entities are found for a given month value.
	 */
	private Supplier<IncomeNotFoundException> createMonthNotFoundException(YearMonth month) {
		return () -> new IncomeNotFoundException(String.format(ErrorMessages.Income.NOT_FOUND_BY_MONTH, month));
	}
}