package com.budgetmaster.testsupport.money.factory;

import com.budgetmaster.money.model.Money;
import com.budgetmaster.testsupport.money.constants.MoneyConstants;

public final class MoneyFactory {
    private MoneyFactory() {}

    public static Money createIncomeMoney() {
        Money money = Money.of(
            MoneyConstants.IncomeDefaults.AMOUNT,
            MoneyConstants.IncomeDefaults.CURRENCY
        );
        return money;
    }

    public static Money createExpenseMoney() {
        Money money = Money.of(
            MoneyConstants.ExpenseDefaults.AMOUNT,
            MoneyConstants.ExpenseDefaults.CURRENCY
        );
        return money;
    }

    public static Money createUpdatedMoney() {
        Money money = Money.of(
            MoneyConstants.Updated.AMOUNT,
            MoneyConstants.Updated.CURRENCY
        );
        return money;
    }

    public static Money createInvalidMoney() {
        Money money = Money.of(
            MoneyConstants.InvalidValues.AMOUNT,
            MoneyConstants.InvalidValues.EUR
        );
        return money;
    }
}
