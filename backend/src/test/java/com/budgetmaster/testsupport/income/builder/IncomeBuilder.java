package com.budgetmaster.testsupport.income.builder;

import com.budgetmaster.income.model.Income;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
import com.budgetmaster.testsupport.money.builder.MoneyBuilder;

import java.time.YearMonth;

import com.budgetmaster.common.enums.TransactionType;

public class IncomeBuilder {
    private Long id = IncomeConstants.Default.ID;
    private String name = IncomeConstants.Default.NAME;
    private String source = IncomeConstants.Default.SOURCE;
    private Money money = MoneyBuilder.defaultIncome().build();
    private TransactionType type = IncomeConstants.Default.TYPE;
    private YearMonth month = IncomeConstants.Default.YEAR_MONTH;

    public IncomeBuilder withId(Long id) {
        this.id = id;
        return this;
    }

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

    public static IncomeBuilder invalidIncome() {
        return new IncomeBuilder()
            .withMoney(MoneyBuilder.invalidMoney().build())
            .withMonth(YearMonth.parse(IncomeConstants.Invalid.YEAR_MONTH));
    }
}