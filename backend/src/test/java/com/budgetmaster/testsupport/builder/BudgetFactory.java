package com.budgetmaster.testsupport.builder;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

public final class BudgetFactory {
    private BudgetFactory() {}

    public static Budget createDefaultBudget() {
        return Budget.of(
            BudgetConstants.Default.YEAR_MONTH,
            BudgetConstants.Default.CURRENCY
        );
    }
}
