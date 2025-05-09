package com.budgetmaster.test.factory;

import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData.IncomeTestConstants;

public final class IncomeTestFactory {
    private IncomeTestFactory() {}

    public static Income createDefaultIncome() {
        Income income = Income.of(
            IncomeTestConstants.Default.NAME,
            IncomeTestConstants.Default.SOURCE,
            Money.of(IncomeTestConstants.Default.AMOUNT, IncomeTestConstants.Default.CURRENCY),
            IncomeTestConstants.Default.TYPE,
            IncomeTestConstants.Default.YEAR_MONTH
        );
        income.setId(IncomeTestConstants.Default.ID);
        return income;
    }

    public static IncomeRequest createDefaultIncomeRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(IncomeTestConstants.Default.AMOUNT);
        moneyRequest.setCurrency(IncomeTestConstants.Default.CURRENCY);

        IncomeRequest request = new IncomeRequest();
        request.setName(IncomeTestConstants.Default.NAME);
        request.setSource(IncomeTestConstants.Default.SOURCE);
        request.setMoney(moneyRequest);
        request.setType(IncomeTestConstants.Default.TYPE);
        request.setMonth(IncomeTestConstants.Default.YEAR_MONTH.toString());
        return request;
    }

    public static IncomeRequest createUpdatedIncomeRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(IncomeTestConstants.Updated.AMOUNT);
        moneyRequest.setCurrency(IncomeTestConstants.Default.CURRENCY);

        IncomeRequest request = new IncomeRequest();
        request.setName(IncomeTestConstants.Updated.NAME);
        request.setSource(IncomeTestConstants.Updated.SOURCE);
        request.setMoney(moneyRequest);
        request.setType(IncomeTestConstants.Updated.TYPE);
        request.setMonth(IncomeTestConstants.Updated.YEAR_MONTH.toString());
        return request;
    }

    public static IncomeRequest createInvalidIncomeRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(IncomeTestConstants.Invalid.AMOUNT);
        moneyRequest.setCurrency(IncomeTestConstants.Default.CURRENCY);

        IncomeRequest request = new IncomeRequest();
        request.setMoney(moneyRequest);
        request.setType(IncomeTestConstants.Default.TYPE);
        request.setMonth(IncomeTestConstants.Invalid.YEAR_MONTH.toString());
        return request;
    }
} 