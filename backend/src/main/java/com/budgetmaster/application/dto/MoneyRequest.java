package com.budgetmaster.application.dto;

import java.math.BigDecimal;
import java.util.Currency;

import com.budgetmaster.application.validation.SupportedCurrencyConstraint;
import com.budgetmaster.constants.validation.ValidationConstraints;
import com.budgetmaster.constants.validation.ValidationMessages;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@SupportedCurrencyConstraint
public class MoneyRequest {
    @NotNull(message = ValidationMessages.Money.AMOUNT_IS_REQUIRED)
    @DecimalMin(value = ValidationConstraints.Monetary.MIN_AMOUNT, 
                inclusive = true, 
                message = ValidationMessages.Money.AMOUNT_MUST_BE_NON_NEGATIVE)
    private BigDecimal amount;

    @NotNull(message = ValidationMessages.Money.CURRENCY_IS_REQUIRED)
    private Currency currency;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
