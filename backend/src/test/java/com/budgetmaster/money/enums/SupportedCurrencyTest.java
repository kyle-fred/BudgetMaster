package com.budgetmaster.money.enums;

import java.util.Currency;

import org.junit.jupiter.api.Test;

import com.budgetmaster.application.enums.SupportedCurrency;
import com.budgetmaster.testsupport.money.constants.MoneyConstants;

import static org.junit.jupiter.api.Assertions.*;

public class SupportedCurrencyTest {
    // -- Test Data --
    private static final Currency GBP = MoneyConstants.GBP;
    private static final Currency EUR = MoneyConstants.InvalidValues.EUR;

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