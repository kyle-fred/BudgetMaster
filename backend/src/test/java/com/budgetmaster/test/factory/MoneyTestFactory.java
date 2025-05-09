package com.budgetmaster.test.factory;

import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData.MoneyTestConstants;

public final class MoneyTestFactory {
    private MoneyTestFactory() {}

    public static Money createIncomeMoney() {
        Money money = Money.of(
            MoneyTestConstants.IncomeDefaults.AMOUNT,
            MoneyTestConstants.IncomeDefaults.CURRENCY
        );
        return money;
    }

    public static Money createExpenseMoney() {
        Money money = Money.of(
            MoneyTestConstants.ExpenseDefaults.AMOUNT,
            MoneyTestConstants.ExpenseDefaults.CURRENCY
        );
        return money;
    }

    public static Money createUpdatedMoney() {
        Money money = Money.of(
            MoneyTestConstants.Updated.AMOUNT,
            MoneyTestConstants.Updated.CURRENCY
        );
        return money;
    }

    public static Money createInvalidMoney() {
        Money money = Money.of(
            MoneyTestConstants.InvalidValues.AMOUNT,
            MoneyTestConstants.InvalidValues.EUR
        );
        return money;
    }
}
