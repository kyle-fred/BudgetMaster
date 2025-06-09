package com.budgetmaster.testsupport.assertions.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;

import com.budgetmaster.application.enums.ExpenseCategory;
import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.builder.model.MoneyBuilder;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

public class ExpenseModelAssertions {

  private final Expense actual;

  public ExpenseModelAssertions(Expense actual) {
    this.actual = actual;
  }

  public static ExpenseModelAssertions assertExpense(Expense actual) {
    assertNotNull(actual);
    return new ExpenseModelAssertions(actual);
  }

  public ExpenseModelAssertions hasName(String expectedName) {
    assertEquals(expectedName, actual.getName());
    return this;
  }

  public ExpenseModelAssertions hasMoney(Money expectedMoney) {
    assertEquals(expectedMoney, actual.getMoney());
    return this;
  }

  public ExpenseModelAssertions hasCategory(ExpenseCategory expectedCategory) {
    assertEquals(expectedCategory, actual.getCategory());
    return this;
  }

  public ExpenseModelAssertions hasType(TransactionType expectedType) {
    assertEquals(expectedType, actual.getType());
    return this;
  }

  public ExpenseModelAssertions hasMonth(YearMonth expectedMonth) {
    assertEquals(expectedMonth, actual.getMonth());
    return this;
  }

  public ExpenseModelAssertions isDefaultExpense() {
    return hasName(ExpenseConstants.Default.NAME)
        .hasMoney(MoneyBuilder.defaultExpense().build())
        .hasCategory(ExpenseConstants.Default.CATEGORY)
        .hasType(ExpenseConstants.Default.TYPE)
        .hasMonth(ExpenseConstants.Default.YEAR_MONTH);
  }

  public ExpenseModelAssertions isUpdatedExpense() {
    return hasName(ExpenseConstants.Updated.NAME)
        .hasMoney(MoneyBuilder.updatedMoney().build())
        .hasCategory(ExpenseConstants.Updated.CATEGORY)
        .hasType(ExpenseConstants.Updated.TYPE)
        .hasMonth(ExpenseConstants.Updated.YEAR_MONTH);
  }
}
