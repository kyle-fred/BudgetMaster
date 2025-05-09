package com.budgetmaster.test.builder;

import java.math.BigDecimal;
import java.util.Currency;

import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.test.constants.TestData.MoneyTestConstants;

public class MoneyRequestTestBuilder {
    private BigDecimal amount;
    private Currency currency = MoneyTestConstants.GBP;

    public MoneyRequestTestBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public MoneyRequestTestBuilder withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public MoneyRequest buildRequest() {
        MoneyRequest request = new MoneyRequest();
        request.setAmount(amount);
        request.setCurrency(currency);
        return request;
    }

    public static MoneyRequestTestBuilder defaultZero () {
        return new MoneyRequestTestBuilder()
            .withAmount(BigDecimal.ZERO);
    }

    public static MoneyRequestTestBuilder defaultIncome() {
        return new MoneyRequestTestBuilder()
            .withAmount(MoneyTestConstants.IncomeDefaults.AMOUNT);
    }

    public static MoneyRequestTestBuilder defaultExpense() {
        return new MoneyRequestTestBuilder()
            .withAmount(MoneyTestConstants.ExpenseDefaults.AMOUNT);
    }

    public static MoneyRequestTestBuilder updatedMoney() {
        return new MoneyRequestTestBuilder()
            .withAmount(MoneyTestConstants.Updated.AMOUNT)
            .withCurrency(MoneyTestConstants.Updated.CURRENCY);
    }

    public static MoneyRequestTestBuilder invalidMoney() {
        return new MoneyRequestTestBuilder()
            .withAmount(MoneyTestConstants.InvalidValues.AMOUNT)
            .withCurrency(MoneyTestConstants.InvalidValues.EUR);
    }
}
