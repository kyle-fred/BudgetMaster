package com.budgetmaster.test.builder;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.test.constants.TestData.IncomeTestConstants;

public class IncomeRequestTestBuilder {
    private String name = IncomeTestConstants.Default.NAME;
    private String source = IncomeTestConstants.Default.SOURCE;
    private MoneyRequest money = MoneyRequestTestBuilder.defaultIncome().buildRequest();
    private TransactionType type = IncomeTestConstants.Default.TYPE;
    private String month = IncomeTestConstants.Default.YEAR_MONTH.toString();

    public IncomeRequestTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public IncomeRequestTestBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    public IncomeRequestTestBuilder withMoney(MoneyRequest money) {
        this.money = money;
        return this;
    }

    public IncomeRequestTestBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public IncomeRequestTestBuilder withMonth(String month) {
        this.month = month;
        return this;
    }

    public IncomeRequest buildRequest() {
        IncomeRequest request = new IncomeRequest();
        request.setName(name);
        request.setSource(source);
        request.setMoney(money);
        request.setType(type);
        request.setMonth(month);
        return request;
    }

    public static IncomeRequestTestBuilder defaultIncomeRequest() {
        return new IncomeRequestTestBuilder();
    }

    public static IncomeRequestTestBuilder updatedIncomeRequest() {
        return new IncomeRequestTestBuilder()
            .withName(IncomeTestConstants.Updated.NAME)
            .withSource(IncomeTestConstants.Updated.SOURCE)
            .withMoney(MoneyRequestTestBuilder.updatedMoney().buildRequest())
            .withType(IncomeTestConstants.Updated.TYPE)
            .withMonth(IncomeTestConstants.Updated.YEAR_MONTH.toString());
    }

    public static IncomeRequestTestBuilder invalidIncomeRequest() {
        return new IncomeRequestTestBuilder()
            .withMoney(MoneyRequestTestBuilder.invalidMoney().buildRequest())
            .withMonth(IncomeTestConstants.Invalid.YEAR_MONTH);
    }
}
