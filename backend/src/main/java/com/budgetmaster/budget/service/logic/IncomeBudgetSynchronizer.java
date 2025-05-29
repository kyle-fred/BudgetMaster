package com.budgetmaster.budget.service.logic;

import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.budget.repository.BudgetRepository;
import com.budgetmaster.common.constants.error.ErrorMessages;
import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.income.model.Income;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomeBudgetSynchronizer {

    private final BudgetRepository budgetRepository;

    public IncomeBudgetSynchronizer(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public void apply(Income income) {
		Budget budget = findOrCreateBudgetFor(income);
		budget.addIncome(income.getMoney().getAmount());
		budgetRepository.save(budget);
	}

	@Transactional
    public void reapply(Income originalIncome, Income updatedIncome) {
		Budget originalBudget = getExistingBudgetFor(originalIncome);
		originalBudget.subtractIncome(originalIncome.getMoney().getAmount());

		Budget targetBudget = findOrCreateBudgetFor(updatedIncome);
		targetBudget.addIncome(updatedIncome.getMoney().getAmount());
		budgetRepository.save(targetBudget);
    }

	public void retract(Income income) {
		Budget budget = getExistingBudgetFor(income);
		budget.subtractIncome(income.getMoney().getAmount());
		budgetRepository.save(budget);
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
	private Budget getExistingBudgetFor(Income income) {
		return budgetRepository.findByMonth(income.getMonth())
			.orElseThrow(() -> new BudgetNotFoundException(String.format(ErrorMessages.Budget.NOT_FOUND_BY_ASSOCIATED_INCOME, income.getMonth())));
	}
}
