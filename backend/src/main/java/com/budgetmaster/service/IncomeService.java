package com.budgetmaster.service;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.repository.IncomeRepository;
import com.budgetmaster.model.Income;

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
		Income income = new Income(
				request.getName(),
				request.getSource(),
				request.getAmount(),
				request.getType()
			);
		return incomeRepository.save(income);
	}
	
	public Optional<Income> getIncomeById(Long id) {
		return incomeRepository.findById(id);
	}
	
	public Optional<Income> updateIncome(Long id, IncomeRequest request) {
		Optional<Income> existingIncome = incomeRepository.findById(id);
		
		if (existingIncome.isPresent()) {
			Income income = existingIncome.get();
			income.setName(request.getName());
			income.setSource(request.getSource());
			income.setAmount(request.getAmount());
			income.setType(request.getType());
			
			return Optional.of(incomeRepository.save(income));
		} else {
			return Optional.empty();
		}
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