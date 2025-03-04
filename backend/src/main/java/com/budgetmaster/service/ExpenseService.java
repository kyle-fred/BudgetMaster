package com.budgetmaster.service;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.repository.ExpenseRepository;
import com.budgetmaster.model.Expense;

import java.util.Optional;

import org.springframework.stereotype.Service;

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
				request.getExpenseType()
			);
		return expenseRepository.save(expense);
	}
	
	public Optional<Expense> getExpenseById(Long id) {
		return expenseRepository.findById(id);
	}
}