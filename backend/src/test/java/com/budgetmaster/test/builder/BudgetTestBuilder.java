package com.budgetmaster.test.builder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.test.constants.TestData.BudgetTestConstants;

public class BudgetTestBuilder {
    private Long id = BudgetTestConstants.Default.ID;
    private BigDecimal totalIncome = BudgetTestConstants.Default.TOTAL_INCOME;
    private BigDecimal totalExpense = BudgetTestConstants.Default.TOTAL_EXPENSE;
    private BigDecimal savings = BudgetTestConstants.Default.SAVINGS;
    private Currency currency = BudgetTestConstants.Default.CURRENCY;
    private YearMonth month = BudgetTestConstants.Default.YEAR_MONTH;

    public BudgetTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public BudgetTestBuilder withTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
        return this;
    }

    public BudgetTestBuilder withTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
        return this;
    }

    public BudgetTestBuilder withSavings(BigDecimal savings) {
        this.savings = savings;
        return this;
    }

    public BudgetTestBuilder withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public BudgetTestBuilder withMonth(YearMonth month) {
        this.month = month;
        return this;
    }

    public Budget build() {
        Budget budget = Budget.of(
            month,
            currency
        );
        budget.setId(id);
        budget.setTotalIncome(totalIncome);
        budget.setTotalExpense(totalExpense);
        budget.setSavings(savings);
        return budget;
    }

    public static BudgetTestBuilder defaultBudget() {
        return new BudgetTestBuilder();
    }

    public static BudgetTestBuilder updatedBudget() {
        return new BudgetTestBuilder()
            .withTotalIncome(BudgetTestConstants.Updated.TOTAL_INCOME)
            .withTotalExpense(BudgetTestConstants.Updated.TOTAL_EXPENSE)
            .withSavings(BudgetTestConstants.Updated.SAVINGS);
    }
}
