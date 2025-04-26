package com.budgetmaster.service;

import com.budgetmaster.constants.error.ErrorMessages.IncomeErrorMessages;
import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.exception.IncomeNotFoundException;
import com.budgetmaster.repository.IncomeRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.utils.model.FinancialModelUtils;
import com.budgetmaster.utils.service.ServiceUtils;
import com.budgetmaster.model.Income;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomeService {
	
	private final IncomeRepository incomeRepository;
	
	public IncomeService(IncomeRepository incomeRepository) {
		this.incomeRepository = incomeRepository;
	}
	
	public Income createIncome(IncomeRequest request) {
		Income income = FinancialModelUtils.buildIncome(request);
		return incomeRepository.saveAndFlush(income);
	}
	
	public List<Income> getAllIncomesForMonth(String monthString) {
		YearMonth month = DateUtils.getValidYearMonth(monthString);
		return ServiceUtils.findListByCustomFinderOrThrow(
			incomeRepository::findByMonth,
			month,
			createMonthNotFoundException(month)
		);
	}
	
	public Income getIncomeById(Long id) {
		return ServiceUtils.findByIdOrThrow(
				incomeRepository,
				id,
				createIdNotFoundException(id)
		);
	}
	
	public Income updateIncome(Long id, IncomeRequest request) {
		Income income = getIncomeById(id);
		FinancialModelUtils.modifyIncome(income, request);
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
		return () -> new IncomeNotFoundException(String.format(IncomeErrorMessages.ERROR_MESSAGE_INCOME_NOT_FOUND_BY_ID, id));
	}
	
	/**
	 * Creates a supplier for IncomeNotFoundException when no entities are found for a given month value.
	 */
	private Supplier<IncomeNotFoundException> createMonthNotFoundException(YearMonth month) {
		return () -> new IncomeNotFoundException(String.format(IncomeErrorMessages.ERROR_MESSAGE_INCOME_NOT_FOUND_BY_MONTH, month));
	}
}