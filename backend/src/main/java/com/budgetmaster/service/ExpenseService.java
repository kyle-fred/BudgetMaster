package com.budgetmaster.service;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.repository.ExpenseRepository;
import com.budgetmaster.model.Expense;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {
	
	private final ExpenseRepository expenseRepository;
	
	public ExpenseService(ExpenseRepository expenseRepository) {
		this.expenseRepository = expenseRepository;
	}
	
	public Expense createExpense(ExpenseRequest request) {
		Expense expense = new Expense(
				request.getName(),
				request.getTarget(),
				request.getAmount(),
				request.getTransactionType()
			);
		return expenseRepository.save(expense);
	}
	
	public Optional<Expense> getExpenseById(Long id) {
		return expenseRepository.findById(id);
	}

		public Optional<Expense> updateExpense(Long id, ExpenseRequest request) {
		Optional<Expense> existingExpense = expenseRepository.findById(id);
		
		if (existingExpense.isPresent()) {
			Expense expense = existingExpense.get();
			expense.setName(request.getName());
			expense.setTarget(request.getTarget());
			expense.setAmount(request.getAmount());
			expense.setTransactionType(request.getTransactionType());
			
			return Optional.of(expenseRepository.save(expense));
		} else {
			return Optional.empty();
		}
	}

	@Transactional
	public boolean deleteExpense(Long id) {
		if (expenseRepository.existsById(id)) {
			expenseRepository.deleteById(id);
			return true;
		}
		return false;
	}
}