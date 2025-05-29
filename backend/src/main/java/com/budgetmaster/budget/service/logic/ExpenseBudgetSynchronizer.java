package com.budgetmaster.budget.service.logic;

import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.common.constants.error.ErrorMessages;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.expense.model.Expense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseBudgetSynchronizer {

    private final BudgetRepository budgetRepository;

    public ExpenseBudgetSynchronizer(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public void apply(Expense expense) {
		Budget budget = findOrCreateBudgetFor(expense);
		budget.addExpense(expense.getMoney().getAmount());
		budgetRepository.save(budget);
	}

	@Transactional
    public void reapply(Expense originalExpense, Expense updatedExpense) {
		Budget originalBudget = getExistingBudgetFor(originalExpense);
		originalBudget.subtractExpense(originalExpense.getMoney().getAmount());

		Budget targetBudget = findOrCreateBudgetFor(updatedExpense);
		targetBudget.addExpense(updatedExpense.getMoney().getAmount());
		budgetRepository.save(targetBudget);
    }

	public void retract(Expense expense) {
		Budget budget = getExistingBudgetFor(expense);
		budget.subtractExpense(expense.getMoney().getAmount());
		budgetRepository.save(budget);
	}

	/**
	 * Returns the budget associated with the given expense. If no budget exists, a new one is created.
	 */
	private Budget findOrCreateBudgetFor(Expense expense) {
		return budgetRepository.findByMonth(expense.getMonth())
			.orElseGet(() -> Budget.of(expense.getMonth(), expense.getMoney().getCurrency()));
	}

	/**
	 * Returns the budget associated with the given income. If no budget exists, an exception is thrown.
	 */
	private Budget getExistingBudgetFor(Expense expense) {
		return budgetRepository.findByMonth(expense.getMonth())
			.orElseThrow(() -> new BudgetNotFoundException(String.format(ErrorMessages.Budget.NOT_FOUND_BY_ASSOCIATED_EXPENSE, expense.getMonth())));
	}
}
