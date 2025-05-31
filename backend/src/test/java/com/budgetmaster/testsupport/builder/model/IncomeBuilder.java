package com.budgetmaster.testsupport.builder.model;

import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

import java.time.YearMonth;

import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.model.Money;

public class IncomeBuilder {

    private String name = IncomeConstants.Default.NAME;
    private String source = IncomeConstants.Default.SOURCE;
    private Money money = MoneyBuilder.defaultIncome().build();
    private TransactionType type = IncomeConstants.Default.TYPE;
    private YearMonth month = IncomeConstants.Default.YEAR_MONTH;

    public IncomeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public IncomeBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    public IncomeBuilder withMoney(Money money) {
        this.money = money;
        return this;
    }

    public IncomeBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public IncomeBuilder withMonth(YearMonth month) {
        this.month = month;
        return this;
    }

    public Income build() {
        Income income = Income.of(name, source, money, type, month);
        return income;
    }

    public static IncomeBuilder defaultIncome() {
        return new IncomeBuilder();
    }

    public static IncomeBuilder updatedIncome() {
        return new IncomeBuilder()
            .withName(IncomeConstants.Updated.NAME)
            .withSource(IncomeConstants.Updated.SOURCE)
            .withMoney(MoneyBuilder.updatedMoney().build())
            .withType(IncomeConstants.Updated.TYPE)
            .withMonth(IncomeConstants.Updated.YEAR_MONTH);
    }
}