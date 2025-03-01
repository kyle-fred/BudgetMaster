package com.budgetmaster.service;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.repository.IncomeRepository;
import com.budgetmaster.model.Income;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class IncomeService {
	
	private final IncomeRepository incomeRepository;
	
	public IncomeService(IncomeRepository incomeRepository) {
		this.incomeRepository = incomeRepository;
	}
	
	public Income createIncome(IncomeRequest request) {
		Income income = new Income(
				request.getName(),
				request.getSource(),
				request.getAmount(),
				request.getIncomeType()
			);
		return incomeRepository.save(income);
	}
	
	public Optional<Income> getIncomeById(Long id) {
		return incomeRepository.findById(id);
	}
}