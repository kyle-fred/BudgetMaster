package com.budgetmaster.test.builder;

import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.IncomeTestConstants;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.common.enums.TransactionType;

public class IncomeTestBuilder {
    private String name = IncomeTestConstants.Default.NAME;
    private String source = IncomeTestConstants.Default.SOURCE;
    private BigDecimal amount = IncomeTestConstants.Default.AMOUNT;
    private Currency currency = IncomeTestConstants.Default.CURRENCY;
    private TransactionType type = IncomeTestConstants.Default.TYPE;
    private YearMonth month = IncomeTestConstants.Default.YEAR_MONTH;
    private Long id = IncomeTestConstants.Default.ID;

    public IncomeTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public IncomeTestBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    public IncomeTestBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public IncomeTestBuilder withCurrency(Currency currency) {
        this.currency = currency;
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

    // Overloaded methods to accomodate for testing invalid/null month's
    public IncomeTestBuilder withMonth(String month) {
        this.month = YearMonth.parse(month);
        return this;
    }

    public IncomeTestBuilder withNullMonth(String month) {
        this.month = null;
        return this;
    }

    public IncomeTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public Income build() {
        Income income = Income.of(
            name,
            source,
            Money.of(amount, currency),
            type,
            month
        );
        income.setId(id);
        return income;
    }

    public IncomeRequest buildRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(amount);
        moneyRequest.setCurrency(currency);

        IncomeRequest request = new IncomeRequest();
        request.setName(name);
        request.setSource(source);
        request.setMoney(moneyRequest);
        request.setType(type);
        request.setMonth(month != null ? month.toString() : null);
        return request;
    }

    public static IncomeTestBuilder defaultIncome() {
        return new IncomeTestBuilder();
    }

    public static IncomeTestBuilder updatedIncome() {
        return new IncomeTestBuilder()
            .withName(IncomeTestConstants.Updated.NAME)
            .withSource(IncomeTestConstants.Updated.SOURCE)
            .withAmount(IncomeTestConstants.Updated.AMOUNT)
            .withType(IncomeTestConstants.Updated.TYPE)
            .withMonth(IncomeTestConstants.Updated.YEAR_MONTH);
    }

    public static IncomeTestBuilder invalidIncome() {
        return new IncomeTestBuilder()
            .withName(IncomeTestConstants.Invalid.NAME)
            .withSource(IncomeTestConstants.Invalid.SOURCE)
            .withAmount(IncomeTestConstants.Invalid.AMOUNT)
            .withMonth(IncomeTestConstants.Invalid.YEAR_MONTH);
    }
} 