package com.budgetmaster.enums;

import java.util.Currency;

import com.budgetmaster.constants.error.ErrorMessages.CurrencyErrorMessages;
import com.budgetmaster.constants.money.CurrencyConstants.CurrencyCodes;

public enum SupportedCurrency {
    GBP(Currency.getInstance(CurrencyCodes.CURRENCY_CODE_GBP));

    private final Currency currency;

    SupportedCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static boolean validateSupportedCurrency(Currency currency) {
        for (SupportedCurrency supported : SupportedCurrency.values()) {
            if (supported.currency.equals(currency)) {
                return true;
            }
        }
        throw new IllegalArgumentException(String.format(CurrencyErrorMessages.ERROR_UNSUPPORTED_CURRENCY, currency));
    }

    public static SupportedCurrency fromCurrency(Currency currency) {
        for (SupportedCurrency supported : SupportedCurrency.values()) {
            if (supported.currency.equals(currency)) {
                return supported;
            }
        }
        throw new IllegalArgumentException(String.format(CurrencyErrorMessages.ERROR_UNSUPPORTED_CURRENCY, currency));
    }
} 