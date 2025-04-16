package com.budgetmaster.dto.money;

import java.math.BigDecimal;
import java.util.Currency;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class MoneyRequest {
    
    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.00", inclusive = true, message = "Amount must be non-negative.")
    private BigDecimal amount;

    @NotNull(message = "Currency must be provided")
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
