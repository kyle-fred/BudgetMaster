package com.budgetmaster.testsupport.income.builder;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
import com.budgetmaster.testsupport.money.builder.MoneyRequestBuilder;

public class IncomeRequestBuilder {
    private String name = IncomeConstants.Default.NAME;
    private String source = IncomeConstants.Default.SOURCE;
    private MoneyRequest money = MoneyRequestBuilder.defaultIncome().buildRequest();
    private TransactionType type = IncomeConstants.Default.TYPE;
    private String month = IncomeConstants.Default.YEAR_MONTH.toString();

    public IncomeRequestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public IncomeRequestBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    public IncomeRequestBuilder withMoney(MoneyRequest money) {
        this.money = money;
        return this;
    }

    public IncomeRequestBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public IncomeRequestBuilder withMonth(String month) {
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

    public static IncomeRequestBuilder defaultIncomeRequest() {
        return new IncomeRequestBuilder();
    }

    public static IncomeRequestBuilder updatedIncomeRequest() {
        return new IncomeRequestBuilder()
            .withName(IncomeConstants.Updated.NAME)
            .withSource(IncomeConstants.Updated.SOURCE)
            .withMoney(MoneyRequestBuilder.updatedMoney().buildRequest())
            .withType(IncomeConstants.Updated.TYPE)
            .withMonth(IncomeConstants.Updated.YEAR_MONTH.toString());
    }

    public static IncomeRequestBuilder invalidIncomeRequest() {
        return new IncomeRequestBuilder()
            .withMoney(MoneyRequestBuilder.invalidMoney().buildRequest())
            .withMonth(IncomeConstants.Invalid.YEAR_MONTH);
    }
}
