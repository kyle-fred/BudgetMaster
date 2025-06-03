package com.budgetmaster.testsupport.assertions.model.list;

import java.util.List;

import com.budgetmaster.application.model.Expense;
import com.budgetmaster.testsupport.assertions.model.ExpenseModelAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseListAssertions {

    private final List<Expense> actualList;

    public ExpenseListAssertions(List<Expense> actualList) {
        this.actualList = actualList;
    }

    public static ExpenseListAssertions assertExpenses(List<Expense> actualList) {
        assertThat(actualList).isNotNull();
        return new ExpenseListAssertions(actualList);
    }

    public ExpenseListAssertions hasSize(int expectedSize) {
        assertThat(actualList).hasSize(expectedSize);
        return this;
    }

    public ExpenseModelAssertions first() {
        return new ExpenseModelAssertions(actualList.get(0));
    }
    
}
