package com.budgetmaster.testsupport.assertions.integration.list;

import java.util.List;

import com.budgetmaster.application.model.Expense;
import com.budgetmaster.testsupport.constants.FieldConstants;

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

    public ExpenseIntegrationListAssertions contains(Expense... expectedExpenses) {
        assertThat(actualList)
            .usingRecursiveComparison()
            .ignoringFields(FieldConstants.Audit.CREATED_AT, FieldConstants.Audit.LAST_UPDATED_AT)
            .isEqualTo(List.of(expectedExpenses));
        return this;
    }
}
