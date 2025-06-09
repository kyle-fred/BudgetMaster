package com.budgetmaster.testsupport.assertions.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;

import com.budgetmaster.application.enums.ExpenseCategory;
import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.model.Money;
import com.budgetmaster.application.repository.ExpenseRepository;
import com.budgetmaster.testsupport.builder.model.MoneyBuilder;
import com.budgetmaster.testsupport.constants.FieldConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

public class ExpenseIntegrationAssertions {

  private final Expense actual;

  public ExpenseIntegrationAssertions(Expense actual) {
    this.actual = actual;
  }

  public static ExpenseIntegrationAssertions assertExpense(Expense actual) {
    assertThat(actual).isNotNull();
    return new ExpenseIntegrationAssertions(actual);
  }

  public static void assertExpenseDeleted(Expense expense, ExpenseRepository expenseRepository) {
    assertThat(expenseRepository.findById(expense.getId())).isEmpty();
  }

  public ExpenseIntegrationAssertions hasId() {
    assertThat(actual.getId()).isNotNull();
    return this;
  }

  public ExpenseIntegrationAssertions hasId(Long expectedId) {
    assertThat(actual.getId()).isEqualTo(expectedId);
    return this;
  }

  public ExpenseIntegrationAssertions hasName(String expectedName) {
    assertThat(actual.getName()).isEqualTo(expectedName);
    return this;
  }

  public ExpenseIntegrationAssertions hasMoney(Money expectedMoney) {
    assertThat(actual.getMoney()).usingRecursiveComparison().isEqualTo(expectedMoney);
    return this;
  }

  public ExpenseIntegrationAssertions hasCategory(ExpenseCategory expectedCategory) {
    assertThat(actual.getCategory()).isEqualTo(expectedCategory);
    return this;
  }

  public ExpenseIntegrationAssertions hasType(TransactionType expectedType) {
    assertThat(actual.getType()).isEqualTo(expectedType);
    return this;
  }

  public ExpenseIntegrationAssertions hasMonth(YearMonth expectedMonth) {
    assertThat(actual.getMonth()).isEqualTo(expectedMonth);
    return this;
  }

  public ExpenseIntegrationAssertions hasCreatedAt() {
    assertThat(actual.getCreatedAt()).isNotNull();
    return this;
  }

  public ExpenseIntegrationAssertions hasUpdatedAt() {
    assertThat(actual.getLastUpdatedAt()).isNotNull();
    return this;
  }

  public ExpenseIntegrationAssertions isDefaultExpense() {
    return hasId()
        .hasName(ExpenseConstants.Default.NAME)
        .hasMoney(MoneyBuilder.defaultExpense().build())
        .hasCategory(ExpenseConstants.Default.CATEGORY)
        .hasType(ExpenseConstants.Default.TYPE)
        .hasMonth(ExpenseConstants.Default.YEAR_MONTH)
        .hasCreatedAt()
        .hasUpdatedAt();
  }

  public ExpenseIntegrationAssertions isUpdatedExpense() {
    return hasId()
        .hasName(ExpenseConstants.Updated.NAME)
        .hasMoney(MoneyBuilder.updatedMoney().build())
        .hasCategory(ExpenseConstants.Updated.CATEGORY)
        .hasType(ExpenseConstants.Updated.TYPE)
        .hasMonth(ExpenseConstants.Updated.YEAR_MONTH)
        .hasCreatedAt()
        .hasUpdatedAt();
  }

  public ExpenseIntegrationAssertions isEqualTo(Expense expected) {
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields(FieldConstants.Audit.CREATED_AT, FieldConstants.Audit.LAST_UPDATED_AT)
        .isEqualTo(expected);
    return this;
  }
}
