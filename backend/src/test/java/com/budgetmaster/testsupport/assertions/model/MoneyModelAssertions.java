package com.budgetmaster.testsupport.assertions.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Currency;

import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

public class MoneyModelAssertions {

  private final Money actual;

  public MoneyModelAssertions(Money actual) {
    this.actual = actual;
  }

  public static MoneyModelAssertions assertMoney(Money actual) {
    assertThat(actual).isNotNull();
    return new MoneyModelAssertions(actual);
  }

  public MoneyModelAssertions hasAmount(BigDecimal expectedAmount) {
    assertThat(actual.getAmount()).isEqualByComparingTo(expectedAmount);
    return this;
  }

  public MoneyModelAssertions hasCurrency(Currency expectedCurrency) {
    assertThat(actual.getCurrency()).isEqualTo(expectedCurrency);
    return this;
  }

  public MoneyModelAssertions isIdenticalTo(Money expected) {
    assertThat(actual).isEqualTo(expected);
    assertThat(actual.hashCode()).isEqualTo(expected.hashCode());
    return this;
  }

  public MoneyModelAssertions isNotEqualTo(Money expected) {
    assertThat(actual).isNotEqualTo(expected);
    return this;
  }

  public MoneyModelAssertions hasMoneyString(String expected) {
    assertThat(actual.toString()).isEqualTo(expected);
    return this;
  }

  public MoneyModelAssertions isDefaultMoney() {
    return hasAmount(MoneyConstants.CreationInputs.BIGDECIMAL_TWO_DP)
        .hasCurrency(MoneyConstants.GBP);
  }

  public MoneyModelAssertions isZeroMoney() {
    return hasAmount(BigDecimal.ZERO).hasCurrency(MoneyConstants.GBP);
  }
}
