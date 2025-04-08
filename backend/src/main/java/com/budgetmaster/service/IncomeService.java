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
	
	public Income createIncome(IncomeRequest request) {
		Income income = FinancialModelUtils.buildIncome(request);
		return incomeRepository.saveAndFlush(income);
	}
	
	public List<Income> getAllIncomesForMonth(String monthYearString) {
		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
		return incomeRepository.findByMonthYear(monthYear);
	}
	
	public Optional<Income> getIncomeById(Long id) {
		return incomeRepository.findById(id);
	}
	
	public Optional<Income> updateIncome(Long id, IncomeRequest request) {
		Optional<Income> existingIncome = incomeRepository.findById(id);
		
		if (existingIncome.isPresent()) {
			Income income = existingIncome.get();
			FinancialModelUtils.modifyIncome(income, request);
			return Optional.of(incomeRepository.saveAndFlush(income));
		}
		return Optional.empty();
	}
	
	@Transactional
	public boolean deleteIncome(Long id) {
		if (incomeRepository.existsById(id)) {
			incomeRepository.deleteById(id);
			return true;
		}
		return false;
	}
}