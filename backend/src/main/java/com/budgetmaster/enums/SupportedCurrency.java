package com.budgetmaster.enums;

import java.util.Currency;

public enum SupportedCurrency {
    GBP(Currency.getInstance("GBP"));

    private final Currency currency;

    SupportedCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static boolean isSupported(Currency currency) {
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
        throw new IllegalArgumentException("Currency " + currency + " is not supported");
    }
} 