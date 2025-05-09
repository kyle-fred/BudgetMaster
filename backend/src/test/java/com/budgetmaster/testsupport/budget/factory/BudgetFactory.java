package com.budgetmaster.testsupport.budget.factory;

import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;

public final class BudgetFactory {
    private BudgetFactory() {}

    public static Budget createDefaultBudget() {
        Budget budget = Budget.of(
            BudgetConstants.Default.YEAR_MONTH,
            BudgetConstants.Default.CURRENCY
        );
        budget.setId(BudgetConstants.Default.ID);
        return budget;
    }
}
