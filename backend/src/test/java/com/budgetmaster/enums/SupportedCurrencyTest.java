package com.budgetmaster.enums;

import java.util.Currency;

import com.budgetmaster.money.enums.SupportedCurrency;
import com.budgetmaster.test.constants.TestData;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SupportedCurrencyTest {
    // -- Test Data --
    private static final Currency GBP = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
    private static final Currency USD = TestData.CurrencyTestDataConstants.CURRENCY_USD;

    // -- Test Methods --

    @Test
    void testGetCurrency() {
        assertEquals(GBP, SupportedCurrency.GBP.getCurrency());
    }

    @Test
    void testValidateSupportedCurrency_WithSupportedCurrency() {
        assertTrue(SupportedCurrency.validateSupportedCurrency(GBP));
    }

    @Test
    void testValidateSupportedCurrency_WithUnsupportedCurrency() {
        assertThrows(IllegalArgumentException.class, () -> SupportedCurrency.validateSupportedCurrency(USD));
    }

    @Test
    void testFromCurrency_WithSupportedCurrency() {
        assertEquals(SupportedCurrency.GBP, SupportedCurrency.fromCurrency(GBP));
    }

    @Test
    void testFromCurrency_WithUnsupportedCurrency() {
        assertThrows(IllegalArgumentException.class, () -> SupportedCurrency.fromCurrency(USD));
    }
} 