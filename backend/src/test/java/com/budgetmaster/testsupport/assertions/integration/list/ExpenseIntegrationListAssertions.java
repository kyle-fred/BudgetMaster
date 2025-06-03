package com.budgetmaster.testsupport.assertions.integration.list;

import java.util.List;

import com.budgetmaster.application.model.Expense;
import com.budgetmaster.testsupport.assertions.integration.ExpenseIntegrationAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseIntegrationListAssertions {

    private final List<Expense> actualList;

    public ExpenseIntegrationListAssertions(List<Expense> actualList) {
        this.actualList = actualList;
    }

    public static ExpenseIntegrationListAssertions assertExpenses(List<Expense> actualList) {
        assertThat(actualList).isNotNull();
        return new ExpenseIntegrationListAssertions(actualList);
    }

    public ExpenseIntegrationListAssertions hasSize(int expectedSize) {
        assertThat(actualList).hasSize(expectedSize);
        return this;
    }

    public ExpenseIntegrationAssertions first() {
        return new ExpenseIntegrationAssertions(actualList.get(0));
    }
}
