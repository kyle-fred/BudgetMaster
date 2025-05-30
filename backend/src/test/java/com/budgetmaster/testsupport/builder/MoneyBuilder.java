package com.budgetmaster.testsupport.builder;

import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

import java.math.BigDecimal;
import java.util.Currency;

public class MoneyBuilder {
    private BigDecimal amount;
    private Currency currency = MoneyConstants.GBP;

    public MoneyBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public MoneyBuilder withCurrency(Currency currency) {
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

    public static MoneyBuilder defaultIncome() {
        return new MoneyBuilder()
            .withAmount(MoneyConstants.IncomeDefaults.AMOUNT);
    }

    public static MoneyBuilder defaultExpense() {
        return new MoneyBuilder()
            .withAmount(MoneyConstants.ExpenseDefaults.AMOUNT);
    }

    public static MoneyBuilder updatedMoney() {
        return new MoneyBuilder()
            .withAmount(MoneyConstants.Updated.AMOUNT)
            .withCurrency(MoneyConstants.Updated.CURRENCY);
    }
}
