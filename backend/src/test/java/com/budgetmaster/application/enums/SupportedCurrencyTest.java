package com.budgetmaster.application.enums;

import java.util.Currency;

import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

import static org.junit.jupiter.api.Assertions.*;

public class SupportedCurrencyTest {

    private static final Currency GBP = MoneyConstants.GBP;
    private static final Currency EUR = MoneyConstants.InvalidValues.EUR;

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
        assertFalse(SupportedCurrency.validateSupportedCurrency(EUR));
    }

    @Test
    void testFromCurrency_WithSupportedCurrency() {
        assertEquals(SupportedCurrency.GBP, SupportedCurrency.fromCurrency(GBP));
    }

    @Test
    void testFromCurrency_WithUnsupportedCurrency() {
        assertThrows(IllegalArgumentException.class, () -> SupportedCurrency.fromCurrency(EUR));
    }
} 