package com.budgetmaster.application.model;

import java.util.Currency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.budgetmaster.testsupport.assertions.model.MoneyModelAssertions;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

import static org.assertj.core.api.Assertions.assertThat;

public class MoneyTest {

    private static final Currency GBP = MoneyConstants.GBP;
    
    @Test
    void testOfBigDecimal() {
        Money money = Money.of(MoneyConstants.CreationInputs.BIGDECIMAL_THREE_DP);

        MoneyModelAssertions.assertMoney(money)
            .isDefaultMoney();
    }

    @Test
    void testOfBigDecimalWithCurrency() {
        Money money = Money.of(MoneyConstants.CreationInputs.BIGDECIMAL_THREE_DP, GBP);

        MoneyModelAssertions.assertMoney(money)
            .isDefaultMoney();
    }

    @Test
    void testOfString() {
        Money money = Money.of(MoneyConstants.CreationInputs.STRING_THREE_DP);

        MoneyModelAssertions.assertMoney(money)
            .isDefaultMoney();
    }

    @Test
    void testOfStringWithCurrency() {
        Money money = Money.of(MoneyConstants.CreationInputs.STRING_THREE_DP, GBP);

        MoneyModelAssertions.assertMoney(money)
            .isDefaultMoney();
    }

    @Test
    void testOfDouble() {
        Money money = Money.of(MoneyConstants.CreationInputs.DOUBLE_THREE_DP);

        MoneyModelAssertions.assertMoney(money)
            .isDefaultMoney();
    }

    @Test
    void testOfDoubleWithCurrency() {
        Money money = Money.of(MoneyConstants.CreationInputs.DOUBLE_THREE_DP, GBP);

        MoneyModelAssertions.assertMoney(money)
            .isDefaultMoney();
    }

    @Test
    void testZero() {
        Money money = Money.zero();

        MoneyModelAssertions.assertMoney(money)
            .isZeroMoney();
    }

    @Test
    void testZeroWithCurrency() {
        Money money = Money.zero(GBP);

        MoneyModelAssertions.assertMoney(money)
            .isZeroMoney();
    }

    // -- Arithmetic Methods --

    @Test
    void testAdd() {
        Money money1 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money money2 = Money.of(MoneyConstants.ArithmeticInputs.FIFTY);
        Money result = money1.add(money2);

        MoneyModelAssertions.assertMoney(result)
            .hasAmount(MoneyConstants.ArithmeticInputs.RESULT_ADD);
    }

    @Test
    void testSubtract() {
        Money money1 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money money2 = Money.of(MoneyConstants.ArithmeticInputs.FIFTY);
        Money result = money1.subtract(money2);

        MoneyModelAssertions.assertMoney(result)
            .hasAmount(MoneyConstants.ArithmeticInputs.RESULT_SUBTRACT);
    }

    @Test
    void testMultiply() {
        Money money = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money result = money.multiply(MoneyConstants.ArithmeticInputs.ONE_POINT_FIVE);

        MoneyModelAssertions.assertMoney(result)
            .hasAmount(MoneyConstants.ArithmeticInputs.RESULT_MULTIPLY);
    }

    @Test
    void testDivide() {
        Money money = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money result = money.divide(MoneyConstants.ArithmeticInputs.THREE);

        MoneyModelAssertions.assertMoney(result)
            .hasAmount(MoneyConstants.ArithmeticInputs.RESULT_DIVIDE);
    }

    // -- Comparison Methods --

    @ParameterizedTest(name = "isGreaterThan({0}, {1}) = {2}")
    @CsvSource({
        "100.00, 50.00, true",
        "50.00, 100.00, false",
        "100.00, 100.00, false"
    })
    void testIsGreaterThan(String amount1, String amount2, boolean expected) {
        Money money1 = Money.of(amount1);
        Money money2 = Money.of(amount2);

        assertThat(money1.isGreaterThan(money2)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "isLessThan({0}, {1}) = {2}")
    @CsvSource({
        "100.00, 50.00, false",
        "50.00, 100.00, true",
        "100.00, 100.00, false"
    })
    void testIsLessThan(String amount1, String amount2, boolean expected) {
        Money money1 = Money.of(amount1);
        Money money2 = Money.of(amount2);

        assertThat(money1.isLessThan(money2)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "isEqualTo({0}, {1}) = {2}")
    @CsvSource({
        "100.00, 100.00, true",
        "100.00, 100.01, false"
    })
    void testIsEqualTo(String amount1, String amount2, boolean expected) {
        Money money1 = Money.of(amount1);
        Money money2 = Money.of(amount2);

        assertThat(money1.isEqualTo(money2)).isEqualTo(expected);
    }

    // -- Rounding Methods --

    @ParameterizedTest(name = "rounding({0}) = {1}")
    @ValueSource(strings = {
        "123.456",
        "123.454",
        "123.455",
        "123.445"
    })
    void testRounding(String amount) {
        Money money = Money.of(amount);

        assertThat(money.getAmount().scale()).isEqualTo(MoneyConstants.SCALE);
    }

    @Test
    void testEqualsAndHashCode() {
        Money money1 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money money2 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money money3 = Money.of(MoneyConstants.Miscellaneous.NINETY_NINE_POINT_NINETY_NINE);
        
        MoneyModelAssertions.assertMoney(money1)
            .isIdenticalTo(money2)
            .isNotEqualTo(money3);
    }

    @Test
    void testToString() {
        Money money = Money.of(MoneyConstants.DisplayStrings.INPUT_STRING);

        MoneyModelAssertions.assertMoney(money)
            .hasMoneyString(MoneyConstants.DisplayStrings.EXPECTED_STRING);
    }

    @Test
    void testToStringWithCurrency() {
        Money money = Money.of(MoneyConstants.DisplayStrings.INPUT_STRING, GBP);

        MoneyModelAssertions.assertMoney(money)
            .hasMoneyString(MoneyConstants.DisplayStrings.EXPECTED_STRING);
    }  
}