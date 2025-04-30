package com.budgetmaster.money.dto;

import java.math.BigDecimal;
import java.util.Currency;

import com.budgetmaster.common.constants.validation.ValidationConstraints;
import com.budgetmaster.common.constants.validation.ValidationMessages;
import com.budgetmaster.money.validation.SupportedCurrencyConstraint;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@SupportedCurrencyConstraint
public class MoneyRequest {
    @NotNull(message = ValidationMessages.Money.REQUIRED_AMOUNT)
    @DecimalMin(value = ValidationConstraints.Monetary.MIN_AMOUNT, 
                inclusive = true, 
                message = ValidationMessages.Money.NON_NEGATIVE_AMOUNT)
    private BigDecimal amount;

    @NotNull(message = ValidationMessages.Money.REQUIRED_CURRENCY)
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
