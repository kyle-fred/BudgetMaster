package com.budgetmaster.testsupport.builder.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

public class BudgetBuilder {

  private BigDecimal totalIncome = BudgetConstants.Default.TOTAL_INCOME;
  private BigDecimal totalExpense = BudgetConstants.Default.TOTAL_EXPENSE;
  private BigDecimal savings = BudgetConstants.Default.SAVINGS;
  private Currency currency = BudgetConstants.Default.CURRENCY;
  private YearMonth month = BudgetConstants.Default.YEAR_MONTH;

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
    Budget budget = Budget.of(month, currency);
    // budget.setId(id);
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
