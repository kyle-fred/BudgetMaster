package com.budgetmaster.budget.service.logic;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.income.model.Income;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomeBudgetSynchronizer {

    private final BudgetRepository budgetRepository;

    public IncomeBudgetSynchronizer(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

	@Transactional
    public void apply(Income income) {
		Budget budget = findOrCreateBudgetFor(income);
		budget.addIncome(income.getMoney().getAmount());
		budgetRepository.save(budget);
	}

	@Transactional
    public void reapply(Income originalIncome, Income updatedIncome) {
		Budget originalBudget = getOriginalBudget(originalIncome);
		originalBudget.subtractIncome(originalIncome.getMoney().getAmount());

		Budget targetBudget = findOrCreateBudgetFor(updatedIncome);
		targetBudget.addIncome(updatedIncome.getMoney().getAmount());
		budgetRepository.save(targetBudget);
    }

	/**
	 * Returns the budget associated with the given income. If no budget exists, a new one is created.
	 */
	private Budget findOrCreateBudgetFor(Income income) {
		return budgetRepository.findByMonth(income.getMonth())
			.orElseGet(() -> Budget.of(income.getMonth(), income.getMoney().getCurrency()));
	}

	/**
	 * Returns the budget associated with the given income. If no budget exists, an exception is thrown.
	 */
	private Budget getOriginalBudget(Income income) {
		return budgetRepository.findByMonth(income.getMonth())
			.orElseThrow(() -> new BudgetNotFoundException("Income's original budget not found for month: " + income.getMonth()));
	}
}
