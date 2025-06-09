package com.budgetmaster.application.enums;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

@DisplayName("SupportedCurrency Enum Tests")
class SupportedCurrencyTest {

  private static final Currency GBP = MoneyConstants.GBP;
  private static final Currency EUR = MoneyConstants.InvalidValues.EUR;

  @Nested
  @DisplayName("Currency Validation Tests")
  class CurrencyValidationTests {

    @Test
    @DisplayName("Should validate supported currency")
    void validateSupportedCurrency_withSupportedCurrency_returnsTrue() {
      assertTrue(SupportedCurrency.validateSupportedCurrency(GBP));
    }

    @Test
    @DisplayName("Should reject unsupported currency")
    void validateSupportedCurrency_withUnsupportedCurrency_returnsFalse() {
      assertFalse(SupportedCurrency.validateSupportedCurrency(EUR));
    }
  }

  @Nested
  @DisplayName("Currency Conversion Tests")
  class CurrencyConversionTests {

    @Test
    @DisplayName("Should convert supported currency to enum")
    void fromCurrency_withSupportedCurrency_returnsMatchingEnum() {
      assertEquals(SupportedCurrency.GBP, SupportedCurrency.fromCurrency(GBP));
    }

    @Test
    @DisplayName("Should throw exception for unsupported currency")
    void fromCurrency_withUnsupportedCurrency_throwsIllegalArgumentException() {
      assertThrows(IllegalArgumentException.class, () -> SupportedCurrency.fromCurrency(EUR));
    }
  }
}
