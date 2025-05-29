package com.budgetmaster.testsupport.income.factory;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.dto.MoneyRequest;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;

public final class IncomeFactory {
    private IncomeFactory() {}

    public static Income createDefaultIncome() {
        return Income.of(
            IncomeConstants.Default.NAME,
            IncomeConstants.Default.SOURCE,
            Money.of(IncomeConstants.Default.AMOUNT, IncomeConstants.Default.CURRENCY),
            IncomeConstants.Default.TYPE,
            IncomeConstants.Default.YEAR_MONTH
        );
    }

    public static IncomeRequest createDefaultIncomeRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(IncomeConstants.Default.AMOUNT);
        moneyRequest.setCurrency(IncomeConstants.Default.CURRENCY);

        IncomeRequest request = new IncomeRequest();
        request.setName(IncomeConstants.Default.NAME);
        request.setSource(IncomeConstants.Default.SOURCE);
        request.setMoney(moneyRequest);
        request.setType(IncomeConstants.Default.TYPE);
        request.setMonth(IncomeConstants.Default.YEAR_MONTH.toString());
        return request;
    }

    public static IncomeRequest createUpdatedIncomeRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(IncomeConstants.Updated.AMOUNT);
        moneyRequest.setCurrency(IncomeConstants.Default.CURRENCY);

        IncomeRequest request = new IncomeRequest();
        request.setName(IncomeConstants.Updated.NAME);
        request.setSource(IncomeConstants.Updated.SOURCE);
        request.setMoney(moneyRequest);
        request.setType(IncomeConstants.Updated.TYPE);
        request.setMonth(IncomeConstants.Updated.YEAR_MONTH.toString());
        return request;
    }

    public static IncomeRequest createInvalidIncomeRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(IncomeConstants.Invalid.AMOUNT);
        moneyRequest.setCurrency(IncomeConstants.Default.CURRENCY);

        IncomeRequest request = new IncomeRequest();
        request.setMoney(moneyRequest);
        request.setType(IncomeConstants.Default.TYPE);
        request.setMonth(IncomeConstants.Invalid.YEAR_MONTH.toString());
        return request;
    }
} 