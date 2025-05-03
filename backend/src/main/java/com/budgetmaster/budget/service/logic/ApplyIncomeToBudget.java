package com.budgetmaster.budget.service.logic;

import org.springframework.stereotype.Service;

import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.income.model.Income;

@Service
public class ApplyIncomeToBudget {

    private final BudgetRepository budgetRepository;

    public ApplyIncomeToBudget(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public void apply(Income income) {
		Budget budget = findOrCreateBudgetFor(income);
		budget.addIncome(income.getMoney().getAmount());
		budgetRepository.save(budget);
	}

	/**
	 * Returns the budget associated with the given income. If no budget exists, a new one is created.
	 */
	private Budget findOrCreateBudgetFor(Income income) {
		return budgetRepository.findByMonth(income.getMonth())
			.orElseGet(() -> Budget.of(income.getMonth(), income.getMoney().getCurrency()));
	}
}
