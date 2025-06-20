package com.budgetmaster.application.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.budgetmaster.application.enums.SupportedCurrency;
import com.budgetmaster.constants.database.ColumnConstraints;
import com.budgetmaster.constants.database.ColumnNames;
import com.budgetmaster.constants.error.ErrorMessages;

@Embeddable
public final class Money {
  private static final int DEFAULT_SCALE = ColumnConstraints.Amount.SCALE;
  private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;
  private static final Currency DEFAULT_CURRENCY = SupportedCurrency.GBP.getCurrency();

  @Column(name = ColumnNames.Money.AMOUNT, nullable = false)
  private BigDecimal amount;

  @Column(name = ColumnNames.Money.CURRENCY, nullable = false)
  private Currency currency;

  protected Money() {}

  private Money(BigDecimal amount, Currency currency) {
    SupportedCurrency.validateSupportedCurrency(currency);
    this.amount = amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    this.currency = currency;
  }

  public static Money of(BigDecimal amount) {
    return new Money(amount, DEFAULT_CURRENCY);
  }

  public static Money of(BigDecimal amount, Currency currency) {
    return new Money(amount, currency);
  }

  public static Money of(String amount) {
    return new Money(new BigDecimal(amount), DEFAULT_CURRENCY);
  }

  public static Money of(String amount, Currency currency) {
    return new Money(new BigDecimal(amount), currency);
  }

  public static Money of(double amount) {
    return new Money(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
  }

  public static Money of(double amount, Currency currency) {
    return new Money(BigDecimal.valueOf(amount), currency);
  }

  public static Money zero() {
    return new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
  }

  public static Money zero(Currency currency) {
    return new Money(BigDecimal.ZERO, currency);
  }

  public Money add(Money other) {
    validateCurrency(other);
    return new Money(amount.add(other.amount), currency);
  }

  public Money subtract(Money other) {
    validateCurrency(other);
    return new Money(amount.subtract(other.amount), currency);
  }

  public Money multiply(BigDecimal multiplier) {
    return new Money(amount.multiply(multiplier), currency);
  }

  public Money divide(BigDecimal divisor) {
    return new Money(amount.divide(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE), currency);
  }

  public boolean isGreaterThan(Money other) {
    validateCurrency(other);
    return amount.compareTo(other.amount) > 0;
  }

  public boolean isLessThan(Money other) {
    validateCurrency(other);
    return amount.compareTo(other.amount) < 0;
  }

  public boolean isEqualTo(Money other) {
    validateCurrency(other);
    return amount.compareTo(other.amount) == 0;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  private void validateCurrency(Money other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException(
          String.format(ErrorMessages.Currency.MISMATCH, this.currency, other.currency));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Money money = (Money) o;
    return amount.equals(money.amount) && currency.equals(money.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, currency);
  }

  @Override
  public String toString() {
    return currency.getSymbol() + amount;
  }
}
