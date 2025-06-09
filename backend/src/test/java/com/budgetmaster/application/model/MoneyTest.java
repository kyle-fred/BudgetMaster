package com.budgetmaster.application.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Currency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.budgetmaster.testsupport.assertions.model.MoneyModelAssertions;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

@DisplayName("Money Model Tests")
class MoneyTest {

  private static final Currency GBP = MoneyConstants.GBP;

  @Nested
  @DisplayName("Money Creation")
  class MoneyCreation {

    @Test
    @DisplayName("Should create money from BigDecimal")
    void of_withBigDecimal_createsMoney() {
      Money money = Money.of(MoneyConstants.CreationInputs.BIGDECIMAL_THREE_DP);

      MoneyModelAssertions.assertMoney(money).isDefaultMoney();
    }

    @Test
    @DisplayName("Should create money from BigDecimal with currency")
    void of_withBigDecimalAndCurrency_createsMoney() {
      Money money = Money.of(MoneyConstants.CreationInputs.BIGDECIMAL_THREE_DP, GBP);

      MoneyModelAssertions.assertMoney(money).isDefaultMoney();
    }

    @Test
    @DisplayName("Should create money from String")
    void of_withString_createsMoney() {
      Money money = Money.of(MoneyConstants.CreationInputs.STRING_THREE_DP);

      MoneyModelAssertions.assertMoney(money).isDefaultMoney();
    }

    @Test
    @DisplayName("Should create money from String with currency")
    void of_withStringAndCurrency_createsMoney() {
      Money money = Money.of(MoneyConstants.CreationInputs.STRING_THREE_DP, GBP);

      MoneyModelAssertions.assertMoney(money).isDefaultMoney();
    }

    @Test
    @DisplayName("Should create money from Double")
    void of_withDouble_createsMoney() {
      Money money = Money.of(MoneyConstants.CreationInputs.DOUBLE_THREE_DP);

      MoneyModelAssertions.assertMoney(money).isDefaultMoney();
    }

    @Test
    @DisplayName("Should create money from Double with currency")
    void of_withDoubleAndCurrency_createsMoney() {
      Money money = Money.of(MoneyConstants.CreationInputs.DOUBLE_THREE_DP, GBP);

      MoneyModelAssertions.assertMoney(money).isDefaultMoney();
    }

    @Test
    @DisplayName("Should create zero money")
    void zero_createsZeroMoney() {
      Money money = Money.zero();

      MoneyModelAssertions.assertMoney(money).isZeroMoney();
    }

    @Test
    @DisplayName("Should create zero money with currency")
    void zero_withCurrency_createsZeroMoney() {
      Money money = Money.zero(GBP);

      MoneyModelAssertions.assertMoney(money).isZeroMoney();
    }
  }

  @Nested
  @DisplayName("Arithmetic Operations")
  class ArithmeticOperations {

    @Test
    @DisplayName("Should add two money amounts")
    void add_withValidAmounts_returnsSum() {
      Money money1 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
      Money money2 = Money.of(MoneyConstants.ArithmeticInputs.FIFTY);
      Money result = money1.add(money2);

      MoneyModelAssertions.assertMoney(result)
          .hasAmount(MoneyConstants.ArithmeticInputs.RESULT_ADD);
    }

    @Test
    @DisplayName("Should subtract two money amounts")
    void subtract_withValidAmounts_returnsDifference() {
      Money money1 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
      Money money2 = Money.of(MoneyConstants.ArithmeticInputs.FIFTY);
      Money result = money1.subtract(money2);

      MoneyModelAssertions.assertMoney(result)
          .hasAmount(MoneyConstants.ArithmeticInputs.RESULT_SUBTRACT);
    }

    @Test
    @DisplayName("Should multiply money amount")
    void multiply_withValidMultiplier_returnsProduct() {
      Money money = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
      Money result = money.multiply(MoneyConstants.ArithmeticInputs.ONE_POINT_FIVE);

      MoneyModelAssertions.assertMoney(result)
          .hasAmount(MoneyConstants.ArithmeticInputs.RESULT_MULTIPLY);
    }

    @Test
    @DisplayName("Should divide money amount")
    void divide_withValidDivisor_returnsQuotient() {
      Money money = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
      Money result = money.divide(MoneyConstants.ArithmeticInputs.THREE);

      MoneyModelAssertions.assertMoney(result)
          .hasAmount(MoneyConstants.ArithmeticInputs.RESULT_DIVIDE);
    }
  }

  @Nested
  @DisplayName("Comparison Operations")
  class ComparisonOperations {

    @ParameterizedTest(name = "isGreaterThan({0}, {1}) = {2}")
    @CsvSource({"100.00, 50.00, true", "50.00, 100.00, false", "100.00, 100.00, false"})
    @DisplayName("Should compare if amount is greater than")
    void isGreaterThan_withValidAmounts_returnsExpectedResult(
        String amount1, String amount2, boolean expected) {
      Money money1 = Money.of(amount1);
      Money money2 = Money.of(amount2);

      assertThat(money1.isGreaterThan(money2)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "isLessThan({0}, {1}) = {2}")
    @CsvSource({"100.00, 50.00, false", "50.00, 100.00, true", "100.00, 100.00, false"})
    @DisplayName("Should compare if amount is less than")
    void isLessThan_withValidAmounts_returnsExpectedResult(
        String amount1, String amount2, boolean expected) {
      Money money1 = Money.of(amount1);
      Money money2 = Money.of(amount2);

      assertThat(money1.isLessThan(money2)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "isEqualTo({0}, {1}) = {2}")
    @CsvSource({"100.00, 100.00, true", "100.00, 100.01, false"})
    @DisplayName("Should compare if amounts are equal")
    void isEqualTo_withValidAmounts_returnsExpectedResult(
        String amount1, String amount2, boolean expected) {
      Money money1 = Money.of(amount1);
      Money money2 = Money.of(amount2);

      assertThat(money1.isEqualTo(money2)).isEqualTo(expected);
    }
  }

  @Nested
  @DisplayName("Rounding Operations")
  class RoundingOperations {

    @ParameterizedTest(name = "rounding({0}) = {1}")
    @ValueSource(strings = {"123.456", "123.454", "123.455", "123.445"})
    @DisplayName("Should round amounts to correct scale")
    void rounding_withValidAmounts_returnsCorrectScale(String amount) {
      Money money = Money.of(amount);

      assertThat(money.getAmount().scale()).isEqualTo(MoneyConstants.SCALE);
    }
  }

  @Nested
  @DisplayName("Object Operations")
  class ObjectOperations {

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void equalsAndHashCode_withValidAmounts_returnsExpectedResults() {
      Money money1 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
      Money money2 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
      Money money3 = Money.of(MoneyConstants.Miscellaneous.NINETY_NINE_POINT_NINETY_NINE);

      MoneyModelAssertions.assertMoney(money1).isIdenticalTo(money2).isNotEqualTo(money3);
    }

    @Test
    @DisplayName("Should convert to string representation")
    void toString_returnsFormattedString() {
      Money money = Money.of(MoneyConstants.DisplayStrings.INPUT_STRING);

      MoneyModelAssertions.assertMoney(money)
          .hasMoneyString(MoneyConstants.DisplayStrings.EXPECTED_STRING);
    }

    @Test
    @DisplayName("Should convert to string representation with currency")
    void toString_withCurrency_returnsFormattedString() {
      Money money = Money.of(MoneyConstants.DisplayStrings.INPUT_STRING, GBP);

      MoneyModelAssertions.assertMoney(money)
          .hasMoneyString(MoneyConstants.DisplayStrings.EXPECTED_STRING);
    }
  }
}
