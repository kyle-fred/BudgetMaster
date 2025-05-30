package com.budgetmaster.testsupport.builder;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.dto.MoneyRequest;
import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

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
}
