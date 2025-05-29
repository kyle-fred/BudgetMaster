package com.budgetmaster.testsupport.budget.factory;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;

public final class BudgetFactory {
    private BudgetFactory() {}

    public static Budget createDefaultBudget() {
        return Budget.of(
            BudgetConstants.Default.YEAR_MONTH,
            BudgetConstants.Default.CURRENCY
        );
    }
}
