package com.budgetmaster.service;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.repository.ExpenseRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.utils.model.FinancialModelUtils;
import com.budgetmaster.model.Expense;

import java.time.YearMonth;
import java.util.List;
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
		Expense expense = FinancialModelUtils.buildExpense(request, request.getMonthYear());
		return expenseRepository.saveAndFlush(expense);
	}
	
	public List<Expense> getAllExpensesForMonth(String monthYearString) {
 		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
 		return expenseRepository.findByMonthYear(monthYear);
 	}
	
	public Optional<Expense> getExpenseById(Long id) {
 		return expenseRepository.findById(id);
 	}
	
	public Optional<Expense> updateExpense(Long id, ExpenseRequest request) {
 		Optional<Expense> existingExpense = expenseRepository.findById(id);
		
		if (existingExpense.isPresent()) {
			Expense expense = existingExpense.get();
			FinancialModelUtils.modifyExpense(expense, request);
			
			return Optional.of(expenseRepository.saveAndFlush(expense));
		}
		return Optional.empty();
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