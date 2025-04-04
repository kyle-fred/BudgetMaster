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
	
	public Expense createExpense(ExpenseRequest request, String monthYearString) {
		Expense expense = FinancialModelUtils.buildExpense(request, monthYearString);
		return expenseRepository.saveAndFlush(expense);
	}
	
	public List<Expense> getAllExpensesForMonth(String monthYearString) {
 		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
 		return expenseRepository.findByMonthYear(monthYear);
 	}
	
	public Optional<Expense> getExpenseById(String monthYearString, Long id) {
 		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
 		return expenseRepository.findByMonthYearAndId(monthYear, id);
 	}
	
	public Optional<Expense> updateExpense(String monthYearString, Long id, ExpenseRequest request) {
 		YearMonth monthYear = DateUtils.getValidYearMonth(monthYearString);
 		Optional<Expense> existingExpense = expenseRepository.findByMonthYearAndId(monthYear, id);
		
		if (existingExpense.isPresent()) {
			Expense expense = existingExpense.get();
			FinancialModelUtils.modifyExpense(expense, monthYear, request);
			
			return Optional.of(expenseRepository.saveAndFlush(expense));
		} else {
			return Optional.empty();
		}
	}

	@Transactional
	public boolean deleteExpense(String monthYearString, Long id) {
		if (expenseRepository.existsById(id)) {
			expenseRepository.deleteById(id);
			return true;
		}
		return false;
	}
}