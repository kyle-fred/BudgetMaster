package com.budgetmaster.dto.money;

import java.math.BigDecimal;
import java.util.Currency;

import com.budgetmaster.constants.validation.ValidationConstraints;
import com.budgetmaster.constants.validation.ValidationMessages.MoneyValidation;
import com.budgetmaster.validation.currency.SupportedCurrencyConstraint;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@SupportedCurrencyConstraint
public class MoneyRequest {
    @NotNull(message = MoneyValidation.VALIDATION_MESSAGE_REQUIRED_AMOUNT)
    @DecimalMin(value = ValidationConstraints.VALIDATION_CONSTRAINT_MIN_MONETARY_AMOUNT, 
                inclusive = true, 
                message = MoneyValidation.VALIDATION_MESSAGE_NON_NEGATIVE_AMOUNT)
    private BigDecimal amount;

    @NotNull(message = MoneyValidation.VALIDATION_MESSAGE_REQUIRED_CURRENCY)
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
