package com.budgetmaster.test.builder;

import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData.MoneyTestConstants;

import java.math.BigDecimal;
import java.util.Currency;

public class MoneyTestBuilder {
    private BigDecimal amount;
    private Currency currency = MoneyTestConstants.GBP;

    public MoneyTestBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public MoneyTestBuilder withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Money build() {
        Money money = Money.of(
            amount,
            currency
        );
        return money;
    }

    public static MoneyTestBuilder defaultIncome() {
        return new MoneyTestBuilder()
            .withAmount(MoneyTestConstants.IncomeDefaults.AMOUNT);
    }

    public static MoneyTestBuilder defaultExpense() {
        return new MoneyTestBuilder()
            .withAmount(MoneyTestConstants.ExpenseDefaults.AMOUNT);
    }

    public static MoneyTestBuilder updatedMoney() {
        return new MoneyTestBuilder()
            .withAmount(MoneyTestConstants.Updated.AMOUNT)
            .withCurrency(MoneyTestConstants.Updated.CURRENCY);
    }

    public static MoneyTestBuilder invalidMoney() {
        return new MoneyTestBuilder()
            .withAmount(MoneyTestConstants.InvalidValues.AMOUNT)
            .withCurrency(MoneyTestConstants.InvalidValues.EUR);
    }
}
