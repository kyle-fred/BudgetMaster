package com.budgetmaster.service;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.repository.IncomeRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.utils.model.FinancialModelUtils;
import com.budgetmaster.model.Income;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomeService {
	
	private final IncomeRepository incomeRepository;
	
	public IncomeService(IncomeRepository incomeRepository) {
		this.incomeRepository = incomeRepository;
	}
	
	public Income createIncome(IncomeRequest request, String monthYearString) {
		Income income = FinancialModelUtils.buildIncome(request, monthYearString);
		return incomeRepository.saveAndFlush(income);
	}
	
	public List<Income> getAllIncomesForMonth(String monthYearString) {
		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
		return incomeRepository.findByMonthYear(monthYear);
	}
	
	public Optional<Income> getIncomeById(String monthYearString, Long id) {
		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
		return incomeRepository.findByMonthYearAndId(monthYear, id);
	}
	
	public Optional<Income> updateIncome(String monthYearString, Long id, IncomeRequest request) {
		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
		Optional<Income> existingIncome = incomeRepository.findByMonthYearAndId(monthYear, id);
		
		if (existingIncome.isPresent()) {
			Income income = existingIncome.get();
			FinancialModelUtils.modifyIncome(income, monthYear, request);
			
			return Optional.of(incomeRepository.saveAndFlush(income));
		} else {
			return Optional.empty();
		}
	}
	
	@Transactional
	public boolean deleteIncome(String monthYearString, Long id) {
		if (incomeRepository.existsById(id)) {
			incomeRepository.deleteById(id);
			return true;
		}
		return false;
	}
}