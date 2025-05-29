package com.budgetmaster.application.service;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.application.service.core.EntityLookupService;
import com.budgetmaster.application.service.synchronization.IncomeBudgetSynchronizer;
import com.budgetmaster.common.constants.error.ErrorMessages;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.exception.IncomeNotFoundException;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomeService extends EntityLookupService {
	
	private final IncomeRepository incomeRepository;
	private final IncomeBudgetSynchronizer incomeBudgetSynchronizer;
	
	public IncomeService(IncomeRepository incomeRepository, IncomeBudgetSynchronizer incomeBudgetSynchronizer) {
		this.incomeRepository = incomeRepository;
		this.incomeBudgetSynchronizer = incomeBudgetSynchronizer;
	}
	
	@Transactional
	public Income createIncome(IncomeRequest request) {
		Income income = incomeRepository.saveAndFlush(Income.from(request));
		incomeBudgetSynchronizer.apply(income);
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
	
	@Transactional
	public Income updateIncome(Long id, IncomeRequest request) {
		Income income = getIncomeById(id);
		Income original = income.deepCopy();
	
		income.updateFrom(request);
		incomeRepository.saveAndFlush(income);
	
		incomeBudgetSynchronizer.reapply(original, income);
		return income;
	}
	
	@Transactional
	public void deleteIncome(Long id) {
		Income income = getIncomeById(id);
		incomeBudgetSynchronizer.retract(income);
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