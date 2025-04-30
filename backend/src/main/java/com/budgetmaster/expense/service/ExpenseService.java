package com.budgetmaster.expense.service;

import com.budgetmaster.common.constants.error.ErrorMessages;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.exception.ExpenseNotFoundException;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.expense.repository.ExpenseRepository;
import com.budgetmaster.utils.service.ServiceUtils;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {
	
	private final ExpenseRepository expenseRepository;
	
	public ExpenseService(ExpenseRepository expenseRepository) {
		this.expenseRepository = expenseRepository;
	}
	
	public Expense createExpense(ExpenseRequest request) {
		Expense expense = Expense.from(request);
		return expenseRepository.saveAndFlush(expense);
	}
	
	public List<Expense> getAllExpensesForMonth(String monthString) {
 		YearMonth month = DateUtils.getValidYearMonth(monthString);
 		return ServiceUtils.findListByCustomFinderOrThrow(
			expenseRepository::findByMonth,
			month,
			createMonthNotFoundException(month)
		);
 	}
	
	public Expense getExpenseById(Long id) {
 		return ServiceUtils.findByIdOrThrow(
			expenseRepository,
			id,
			createIdNotFoundException(id)
		);
 	}
	
	public Expense updateExpense(Long id, ExpenseRequest request) {
 		Expense expense = getExpenseById(id);
		expense.updateFrom(request);
		return expenseRepository.saveAndFlush(expense);
	}

	@Transactional
	public void deleteExpense(Long id) {
		getExpenseById(id);
		expenseRepository.deleteById(id);
	}

	/**
	 * Creates a supplier for ExpenseNotFoundException when entity is not found by ID.
	 */
	private Supplier<ExpenseNotFoundException> createIdNotFoundException(Long id) {
		return () -> new ExpenseNotFoundException(String.format(ErrorMessages.Expense.NOT_FOUND_BY_ID, id));
		}

	/**
	 * Creates a supplier for ExpenseNotFoundException when no entities are found for a given month value.
	 */
	private Supplier<ExpenseNotFoundException> createMonthNotFoundException(YearMonth month) {
		return () -> new ExpenseNotFoundException(String.format(ErrorMessages.Expense.NOT_FOUND_BY_MONTH, month));
	}
}