package com.budgetmaster.model.value;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {
    // -- Test Data --
    private static final Currency GBP = Currency.getInstance("GBP");

    // -- Creation Methods --
    
    @Test
    void testOfBigDecimal() {
        Money money = Money.of(new BigDecimal("123.456"));
        assertEquals(new BigDecimal("123.46"), money.getAmount(), "Amount should be rounded to 2 decimal places");
        assertEquals(GBP, money.getCurrency(), "Default currency should be set correctly");
    }

    @Test
    void testOfBigDecimalWithCurrency() {
        Money money = Money.of(new BigDecimal("123.456"), GBP);
        assertEquals(new BigDecimal("123.46"), money.getAmount(), "Amount should be rounded to 2 decimal places");
        assertEquals(GBP, money.getCurrency(), "Currency should be set correctly");
    }

    @Test
    void testOfString() {
        Money money = Money.of("123.456");
        assertEquals(new BigDecimal("123.46"), money.getAmount(), "Amount should be rounded to 2 decimal places");
        assertEquals(GBP, money.getCurrency(), "Default currency should be set correctly");
    }

    @Test
    void testOfStringWithCurrency() {
        Money money = Money.of("123.456", GBP);
        assertEquals(new BigDecimal("123.46"), money.getAmount(), "Amount should be rounded to 2 decimal places");
        assertEquals(GBP, money.getCurrency(), "Currency should be set correctly");
    }

    @Test
    void testOfDouble() {
        Money money = Money.of(123.456);
        assertEquals(new BigDecimal("123.46"), money.getAmount(), "Amount should be rounded to 2 decimal places");
        assertEquals(GBP, money.getCurrency(), "Default currency should be set correctly");
    }

    @Test
    void testOfDoubleWithCurrency() {
        Money money = Money.of(123.456, GBP);
        assertEquals(new BigDecimal("123.46"), money.getAmount(), "Amount should be rounded to 2 decimal places");
        assertEquals(GBP, money.getCurrency(), "Currency should be set correctly");
    }

    // -- Zero Methods --

    @Test
    void testZero() {
        Money money = Money.zero();
        assertEquals(0, money.getAmount().compareTo(BigDecimal.ZERO), "Amount should be zero");
        assertEquals(GBP, money.getCurrency(), "Default currency should be set correctly");
    }

    @Test
    void testZeroWithCurrency() {
        Money money = Money.zero(GBP);
        assertEquals(0, money.getAmount().compareTo(BigDecimal.ZERO), "Amount should be zero");
        assertEquals(GBP, money.getCurrency(), "Currency should be set correctly");
    }

    // -- Arithmetic Methods --

    @Test
    void testAdd() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("50.00");
        Money result = money1.add(money2);
        assertEquals(new BigDecimal("150.00"), result.getAmount(), "Amount should be added correctly");
    }

    @Test
    void testSubtract() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("50.00");
        Money result = money1.subtract(money2);
        assertEquals(new BigDecimal("50.00"), result.getAmount(), "Amount should be subtracted correctly");
    }

    @Test
    void testMultiply() {
        Money money = Money.of("100.00");
        Money result = money.multiply(new BigDecimal("1.5"));
        assertEquals(new BigDecimal("150.00"), result.getAmount(), "Amount should be multiplied correctly");
    }

    @Test
    void testDivide() {
        Money money = Money.of("100.00");
        Money result = money.divide(new BigDecimal("3"));
        assertEquals(new BigDecimal("33.33"), result.getAmount(), "Amount should be divided correctly");
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
        assertEquals(expected, money1.isGreaterThan(money2), "Should return true if amount1 is greater than amount2");
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
        assertEquals(expected, money1.isLessThan(money2), "Should return true if amount1 is less than amount2");
    }

    @ParameterizedTest(name = "isEqualTo({0}, {1}) = {2}")
    @CsvSource({
        "100.00, 100.00, true",
        "100.00, 100.01, false"
    })
    void testIsEqualTo(String amount1, String amount2, boolean expected) {
        Money money1 = Money.of(amount1);
        Money money2 = Money.of(amount2);
        assertEquals(expected, money1.isEqualTo(money2), "Should return true if amount1 is equal to amount2");
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
        assertEquals(2, money.getAmount().scale(), "Amount should be rounded to 2 decimal places");
    }

    // -- Equality and HashCode Methods --

    @Test
    void testEqualsAndHashCode() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("100.00");
        Money money3 = Money.of("99.99");
        
        assertEquals(money1, money2, "Should return true if Money objects have the same amount and currency");
        assertEquals(money1.hashCode(), money2.hashCode(), "Hash code should be equal for Money objects with the same amount and currency");
        assertNotEquals(money1, money3, "Should return false if Money objects have any different attributes");
    }

    @Test
    void testToString() {
        Money money = Money.of("100.00");
        assertEquals("£100.00", money.toString(), "Should return the correct string representation of the Money object");
    }

    @Test
    void testToStringWithCurrency() {
        Money money = Money.of("100.00", GBP);
        assertEquals("£100.00", money.toString(), "Should return the correct string representation of the Money object with the specified currency");
    }  
}