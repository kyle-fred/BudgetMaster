package com.budgetmaster.testsupport.assertions.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.testsupport.constants.FieldConstants;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

public class BudgetIntegrationAssertions {

  private final Budget actual;

  public BudgetIntegrationAssertions(Budget actual) {
    this.actual = actual;
  }

  public static BudgetIntegrationAssertions assertBudget(Budget actual) {
    assertThat(actual).isNotNull();
    return new BudgetIntegrationAssertions(actual);
  }

  public static void assertBudgetNotInitialized(Budget budget) {
    assertThat(budget).isNull();
  }

  public static void assertBudgetDeleted(Budget budget, BudgetRepository budgetRepository) {
    assertThat(budgetRepository.findById(budget.getId())).isEmpty();
  }

  public BudgetIntegrationAssertions hasId() {
    assertThat(actual.getId()).isNotNull();
    return this;
  }

  public BudgetIntegrationAssertions hasTotalIncome(BigDecimal expectedTotalIncome) {
    assertThat(actual.getTotalIncome()).isEqualByComparingTo(expectedTotalIncome);
    return this;
  }

  public BudgetIntegrationAssertions hasTotalExpense(BigDecimal expectedTotalExpense) {
    assertThat(actual.getTotalExpense()).isEqualByComparingTo(expectedTotalExpense);
    return this;
  }

  public BudgetIntegrationAssertions hasSavings(BigDecimal expectedSavings) {
    assertThat(actual.getSavings()).isEqualByComparingTo(expectedSavings);
    return this;
  }

  public BudgetIntegrationAssertions hasCurrency(Currency expectedCurrency) {
    assertThat(actual.getCurrency()).isEqualTo(expectedCurrency);
    return this;
  }

  public BudgetIntegrationAssertions hasMonth(YearMonth expectedMonth) {
    assertThat(actual.getMonth()).isEqualTo(expectedMonth);
    return this;
  }

  public BudgetIntegrationAssertions hasCreatedAt() {
    assertThat(actual.getCreatedAt()).isNotNull();
    return this;
  }

  public BudgetIntegrationAssertions hasUpdatedAt() {
    assertThat(actual.getLastUpdatedAt()).isNotNull();
    return this;
  }

  public BudgetIntegrationAssertions isDefaultBudget() {
    return hasId()
        .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME)
        .hasTotalExpense(BudgetConstants.Default.TOTAL_EXPENSE)
        .hasSavings(BudgetConstants.Default.SAVINGS)
        .hasCurrency(BudgetConstants.Default.CURRENCY)
        .hasMonth(BudgetConstants.Default.YEAR_MONTH)
        .hasCreatedAt()
        .hasUpdatedAt();
  }

  public BudgetIntegrationAssertions isEqualTo(Budget expected) {
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields(FieldConstants.Audit.CREATED_AT, FieldConstants.Audit.LAST_UPDATED_AT)
        .isEqualTo(expected);
    return this;
  }
}
