package com.budgetmaster.test.builder;

import com.budgetmaster.income.model.Income;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData.IncomeTestConstants;

import java.time.YearMonth;

import com.budgetmaster.common.enums.TransactionType;

public class IncomeTestBuilder {
    private Long id = IncomeTestConstants.Default.ID;
    private String name = IncomeTestConstants.Default.NAME;
    private String source = IncomeTestConstants.Default.SOURCE;
    private Money money = MoneyTestBuilder.defaultIncome().build();
    private TransactionType type = IncomeTestConstants.Default.TYPE;
    private YearMonth month = IncomeTestConstants.Default.YEAR_MONTH;

    public IncomeTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public IncomeTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public IncomeTestBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    public IncomeTestBuilder withMoney(Money money) {
        this.money = money;
        return this;
    }

    public IncomeTestBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public IncomeTestBuilder withMonth(YearMonth month) {
        this.month = month;
        return this;
    }

    public Income build() {
        Income income = Income.of(
            name,
            source,
            money,
            type,
            month
        );
        income.setId(id);
        return income;
    }

    public static IncomeTestBuilder defaultIncome() {
        return new IncomeTestBuilder();
    }

    public static IncomeTestBuilder updatedIncome() {
        return new IncomeTestBuilder()
            .withName(IncomeTestConstants.Updated.NAME)
            .withSource(IncomeTestConstants.Updated.SOURCE)
            .withMoney(MoneyTestBuilder.updatedMoney().build())
            .withType(IncomeTestConstants.Updated.TYPE)
            .withMonth(IncomeTestConstants.Updated.YEAR_MONTH);
    }

    public static IncomeTestBuilder invalidIncome() {
        return new IncomeTestBuilder()
            .withMoney(MoneyTestBuilder.invalidMoney().build())
            .withMonth(YearMonth.parse(IncomeTestConstants.Invalid.YEAR_MONTH));
    }
}