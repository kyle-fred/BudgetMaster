package com.budgetmaster.enums;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

import org.junit.jupiter.api.Test;

class SupportedCurrencyTest {
    // -- Test Data --
    private static final Currency GBP = Currency.getInstance("GBP");
    private static final Currency USD = Currency.getInstance("USD");

    // -- Test Methods --

    @Test
    void testGetCurrency() {
        assertEquals(GBP, SupportedCurrency.GBP.getCurrency(), "Should return the correct Currency instance");
    }

    @Test
    void testValidateSupportedCurrency_WithSupportedCurrency() {
        assertTrue(SupportedCurrency.validateSupportedCurrency(GBP), "Should return true for supported currency");
    }

    @Test
    void testValidateSupportedCurrency_WithUnsupportedCurrency() {
        assertThrows(IllegalArgumentException.class, () -> SupportedCurrency.validateSupportedCurrency(USD),
            "Should throw IllegalArgumentException for unsupported currency");
    }

    @Test
    void testFromCurrency_WithSupportedCurrency() {
        assertEquals(SupportedCurrency.GBP, SupportedCurrency.fromCurrency(GBP), 
            "Should return the correct enum value for supported currency");
    }

    @Test
    void testFromCurrency_WithUnsupportedCurrency() {
        assertThrows(IllegalArgumentException.class, () -> SupportedCurrency.fromCurrency(USD),
            "Should throw IllegalArgumentException for unsupported currency");
    }
} 