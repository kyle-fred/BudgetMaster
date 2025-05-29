package com.budgetmaster.application.enums;

import java.util.Currency;

import com.budgetmaster.common.constants.error.ErrorMessages;
import com.budgetmaster.common.constants.money.CurrencyConstants;

public enum SupportedCurrency {
    GBP(Currency.getInstance(CurrencyConstants.Codes.GBP));

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
        return false;
    }

    public static SupportedCurrency fromCurrency(Currency currency) {
        for (SupportedCurrency supported : SupportedCurrency.values()) {
            if (supported.currency.equals(currency)) {
                return supported;
            }
        }
        throw new IllegalArgumentException(String.format(ErrorMessages.Currency.UNSUPPORTED, currency));
    }
} 