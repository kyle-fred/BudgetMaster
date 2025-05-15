package com.budgetmaster.testsupport.budget.builder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.budget.factory.BudgetFactory;

public class BudgetBuilder {
    private Long id = BudgetConstants.Default.ID;
    private BigDecimal totalIncome = BudgetConstants.Default.TOTAL_INCOME;
    private BigDecimal totalExpense = BudgetConstants.Default.TOTAL_EXPENSE;
    private BigDecimal savings = BudgetConstants.Default.SAVINGS;
    private Currency currency = BudgetConstants.Default.CURRENCY;
    private YearMonth month = BudgetConstants.Default.YEAR_MONTH;

    public BudgetBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public BudgetBuilder withTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
        return this;
    }

    public BudgetBuilder withTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
        return this;
    }

    public BudgetBuilder withSavings(BigDecimal savings) {
        this.savings = savings;
        return this;
    }

    public BudgetBuilder withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public BudgetBuilder withMonth(YearMonth month) {
        this.month = month;
        return this;
    }

    public Budget build() {
        Budget budget = BudgetFactory.createDefaultBudget();
        budget.setId(id);
        budget.setCurrency(currency);
        budget.setMonth(month);
        budget.setTotalIncome(totalIncome);
        budget.setTotalExpense(totalExpense);
        budget.setSavings(savings);
        return budget;
    }

    public static BudgetBuilder defaultBudget() {
        return new BudgetBuilder();
    }

    public static BudgetBuilder zeroedBudget() {
        return new BudgetBuilder()
            .withTotalIncome(BigDecimal.ZERO)
            .withTotalExpense(BigDecimal.ZERO)
            .withSavings(BigDecimal.ZERO);
    }
}
