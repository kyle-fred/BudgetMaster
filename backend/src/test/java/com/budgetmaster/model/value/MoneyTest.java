package com.budgetmaster.model.value;

import java.math.BigDecimal;
import java.util.Currency;

import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


public class MoneyTest {
    // -- Test Data --
    private static final Currency GBP = TestData.CurrencyTestDataConstants.CURRENCY_GBP;

    // -- Creation Methods --
    
    @Test
    void testOfBigDecimal() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_THREE_DECIMALS);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_TWO_DECIMALS, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfBigDecimalWithCurrency() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_THREE_DECIMALS, GBP);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_TWO_DECIMALS, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfString() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_THREE_DECIMALS_STRING);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_TWO_DECIMALS, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfStringWithCurrency() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_THREE_DECIMALS_STRING, GBP);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_TWO_DECIMALS, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfDouble() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_THREE_DECIMALS_DOUBLE);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_TWO_DECIMALS, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    @Test
    void testOfDoubleWithCurrency() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_THREE_DECIMALS_DOUBLE, GBP);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_TWO_DECIMALS, money.getAmount());
        assertEquals(GBP, money.getCurrency());
    }

    // -- Zero Methods --

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

    // -- Arithmetic Methods --

    @Test
    void testAdd() {
        Money money1 = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED);
        Money money2 = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_FIFTY);
        Money result = money1.add(money2);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED_FIFTY, result.getAmount());
    }

    @Test
    void testSubtract() {
        Money money1 = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED);
        Money money2 = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_FIFTY);
        Money result = money1.subtract(money2);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_FIFTY, result.getAmount());
    }

    @Test
    void testMultiply() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED);
        Money result = money.multiply(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_POINT_FIVE);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED_FIFTY, result.getAmount());
    }

    @Test
    void testDivide() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED);
        Money result = money.divide(TestData.MoneyDtoTestDataConstants.AMOUNT_THREE);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_THIRTY_THREE_RECURRING, result.getAmount());
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

    // -- Equality and HashCode Methods --

    @Test
    void testEqualsAndHashCode() {
        Money money1 = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED);
        Money money2 = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED);
        Money money3 = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_NINETY_NINE_POINT_NINETY_NINE);
        
        assertEquals(money1, money2);
        assertEquals(money1.hashCode(), money2.hashCode());
        assertNotEquals(money1, money3);
    }

    @Test
    void testToString() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED_STRING);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED_POUNDS_STRING, money.toString());
    }

    @Test
    void testToStringWithCurrency() {
        Money money = Money.of(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED_STRING, GBP);
        assertEquals(TestData.MoneyDtoTestDataConstants.AMOUNT_ONE_HUNDRED_POUNDS_STRING, money.toString());
    }  
}