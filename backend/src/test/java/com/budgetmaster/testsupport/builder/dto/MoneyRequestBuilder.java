package com.budgetmaster.testsupport.builder.dto;

import java.math.BigDecimal;
import java.util.Currency;

import com.budgetmaster.application.dto.MoneyRequest;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

public class MoneyRequestBuilder {
    private BigDecimal amount;
    private Currency currency = MoneyConstants.GBP;

    public MoneyRequestBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public MoneyRequestBuilder withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public MoneyRequest buildRequest() {
        MoneyRequest request = new MoneyRequest();
        request.setAmount(amount);
        request.setCurrency(currency);
        return request;
    }

    public static MoneyRequestBuilder defaultZero () {
        return new MoneyRequestBuilder()
            .withAmount(BigDecimal.ZERO);
    }

    public static MoneyRequestBuilder defaultIncome() {
        return new MoneyRequestBuilder()
            .withAmount(MoneyConstants.IncomeDefaults.AMOUNT);
    }

    public static MoneyRequestBuilder defaultExpense() {
        return new MoneyRequestBuilder()
            .withAmount(MoneyConstants.ExpenseDefaults.AMOUNT);
    }

    public static MoneyRequestBuilder updatedMoney() {
        return new MoneyRequestBuilder()
            .withAmount(MoneyConstants.Updated.AMOUNT)
            .withCurrency(MoneyConstants.Updated.CURRENCY);
    }

    public static MoneyRequestBuilder invalidMoney() {
        return new MoneyRequestBuilder()
            .withAmount(MoneyConstants.InvalidValues.AMOUNT)
            .withCurrency(MoneyConstants.InvalidValues.EUR);
    }
}
