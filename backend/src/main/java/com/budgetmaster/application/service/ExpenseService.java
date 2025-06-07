package com.budgetmaster.application.service;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.application.exception.ExpenseNotFoundException;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.repository.ExpenseRepository;
import com.budgetmaster.application.service.core.EntityLookupService;
import com.budgetmaster.application.service.synchronization.ExpenseBudgetSynchronizer;
import com.budgetmaster.application.util.DateUtils;
import com.budgetmaster.constants.error.ErrorMessages;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService extends EntityLookupService {
	
	private final ExpenseRepository expenseRepository;
	private final ExpenseBudgetSynchronizer expenseBudgetSynchronizer;
	
	public ExpenseService(ExpenseRepository expenseRepository, ExpenseBudgetSynchronizer expenseBudgetSynchronizer) {
		this.expenseRepository = expenseRepository;
		this.expenseBudgetSynchronizer = expenseBudgetSynchronizer;
	}
	
	@Transactional
	public Expense createExpense(ExpenseRequest request) {
		Expense expense = expenseRepository.saveAndFlush(Expense.from(request));
		expenseBudgetSynchronizer.apply(expense);
		return expense;
	}
	
	public List<Expense> getAllExpensesForMonth(String monthString) {
 		YearMonth month = DateUtils.getValidYearMonth(monthString);
 		return findListByCustomFinderOrThrow(
			expenseRepository::findByMonth,
			month,
			createMonthNotFoundException(month)
		);
 	}
	
	public Expense getExpenseById(Long id) {
 		return findByIdOrThrow(
			expenseRepository,
			id,
			createIdNotFoundException(id)
		);
 	}
	
	@Transactional
	public Expense updateExpense(Long id, ExpenseRequest request) {
 		Expense expense = getExpenseById(id);
		Expense original = expense.deepCopy();

		expense.updateFrom(request);
		expenseRepository.saveAndFlush(expense);

		expenseBudgetSynchronizer.reapply(original, expense);
		return expense;
	}

	@Transactional
	public void deleteExpense(Long id) {
		Expense expense = getExpenseById(id);
		expenseBudgetSynchronizer.retract(expense);
		expenseRepository.deleteById(id);
	}

	/**
	 * Creates a supplier for ExpenseNotFoundException when entity is not found by ID.
	 */
	private Supplier<ExpenseNotFoundException> createIdNotFoundException(Long id) {
		return () -> new ExpenseNotFoundException(String.format(ErrorMessages.Expense.NOT_FOUND_WITH_ID, id));
	}

	/**
	 * Creates a supplier for ExpenseNotFoundException when no entities are found for a given month value.
	 */
	private Supplier<ExpenseNotFoundException> createMonthNotFoundException(YearMonth month) {
		return () -> new ExpenseNotFoundException(String.format(ErrorMessages.Expense.NOT_FOUND_FOR_MONTH, month));
	}
}