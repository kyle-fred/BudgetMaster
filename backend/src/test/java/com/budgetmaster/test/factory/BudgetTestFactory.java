package com.budgetmaster.test.factory;

import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.test.constants.TestData.BudgetTestConstants;

public final class BudgetTestFactory {
    private BudgetTestFactory() {}

    public static Budget createDefaultBudget() {
        Budget budget = Budget.of(
            BudgetTestConstants.Default.YEAR_MONTH,
            BudgetTestConstants.Default.CURRENCY
        );
        budget.setId(BudgetTestConstants.Default.ID);
        return budget;
    }
}
