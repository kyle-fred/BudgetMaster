package com.budgetmaster.money.model;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.budgetmaster.testsupport.money.constants.MoneyConstants;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    // -- Test Data --
    private static final Currency GBP = MoneyConstants.GBP;
    
    @Test
    void testOfBigDecimal() {
        Money money = Money.of(MoneyConstants.CreationInputs.BIGDECIMAL_THREE_DP);
        assertEquals(MoneyConstants.CreationInputs.BIGDECIMAL_TWO_DP, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfBigDecimalWithCurrency() {
        Money money = Money.of(MoneyConstants.CreationInputs.BIGDECIMAL_THREE_DP, GBP);
        assertEquals(MoneyConstants.CreationInputs.BIGDECIMAL_TWO_DP, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfString() {
        Money money = Money.of(MoneyConstants.CreationInputs.STRING_THREE_DP);
        assertEquals(MoneyConstants.CreationInputs.BIGDECIMAL_TWO_DP, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfStringWithCurrency() {
        Money money = Money.of(MoneyConstants.CreationInputs.STRING_THREE_DP, GBP);
        assertEquals(MoneyConstants.CreationInputs.BIGDECIMAL_TWO_DP, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfDouble() {
        Money money = Money.of(MoneyConstants.CreationInputs.DOUBLE_THREE_DP);
        assertEquals(MoneyConstants.CreationInputs.BIGDECIMAL_TWO_DP, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfDoubleWithCurrency() {
        Money money = Money.of(MoneyConstants.CreationInputs.DOUBLE_THREE_DP, GBP);
        assertEquals(MoneyConstants.CreationInputs.BIGDECIMAL_TWO_DP, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testZero() {
        Money money = Money.zero();
        assertEquals(0, money.getAmount().compareTo(BigDecimal.ZERO));
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testZeroWithCurrency() {
        Money money = Money.zero(GBP);
        assertEquals(0, money.getAmount().compareTo(BigDecimal.ZERO));
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testAdd() {
        Money money1 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money money2 = Money.of(MoneyConstants.ArithmeticInputs.FIFTY);
        Money result = money1.add(money2);
        assertEquals(MoneyConstants.ArithmeticInputs.RESULT_ADD, result.getAmount());
    }

    @Test
    void testSubtract() {
        Money money1 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money money2 = Money.of(MoneyConstants.ArithmeticInputs.FIFTY);
        Money result = money1.subtract(money2);
        assertEquals(MoneyConstants.ArithmeticInputs.RESULT_SUBTRACT, result.getAmount());
    }

    @Test
    void testMultiply() {
        Money money = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money result = money.multiply(MoneyConstants.ArithmeticInputs.ONE_POINT_FIVE);
        assertEquals(MoneyConstants.ArithmeticInputs.RESULT_MULTIPLY, result.getAmount());
    }

    @Test
    void testDivide() {
        Money money = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money result = money.divide(MoneyConstants.ArithmeticInputs.THREE);
        assertEquals(MoneyConstants.ArithmeticInputs.RESULT_DIVIDE, result.getAmount());
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
        assertEquals(expected, money1.isGreaterThan(money2));
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
        assertEquals(expected, money1.isLessThan(money2));
    }

    @ParameterizedTest(name = "isEqualTo({0}, {1}) = {2}")
    @CsvSource({
        "100.00, 100.00, true",
        "100.00, 100.01, false"
    })
    void testIsEqualTo(String amount1, String amount2, boolean expected) {
        Money money1 = Money.of(amount1);
        Money money2 = Money.of(amount2);
        assertEquals(expected, money1.isEqualTo(money2));
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
        assertEquals(2, money.getAmount().scale());
    }

    @Test
    void testEqualsAndHashCode() {
        Money money1 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money money2 = Money.of(MoneyConstants.ArithmeticInputs.HUNDRED);
        Money money3 = Money.of(MoneyConstants.Miscellaneous.NINETY_NINE_POINT_NINETY_NINE);
        
        assertEquals(money1, money2);
        assertEquals(money1.hashCode(), money2.hashCode());
        assertNotEquals(money1, money3);
    }

    @Test
    void testToString() {
        Money money = Money.of(MoneyConstants.DisplayStrings.INPUT_STRING);
        assertEquals(MoneyConstants.DisplayStrings.EXPECTED_STRING, money.toString());
    }

    @Test
    void testToStringWithCurrency() {
        Money money = Money.of(MoneyConstants.DisplayStrings.INPUT_STRING, GBP);
        assertEquals(MoneyConstants.DisplayStrings.EXPECTED_STRING, money.toString());
    }  
}