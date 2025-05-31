package com.budgetmaster.testsupport.builder.dto;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.application.dto.MoneyRequest;
import com.budgetmaster.application.enums.ExpenseCategory;
import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.testsupport.builder.MoneyRequestBuilder;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

public class ExpenseRequestBuilder {
    private String name = ExpenseConstants.Default.NAME;
    private ExpenseCategory category = ExpenseConstants.Default.CATEGORY;
    private MoneyRequest money = MoneyRequestBuilder.defaultExpense().buildRequest();
    private TransactionType type = ExpenseConstants.Default.TYPE;
    private String month = ExpenseConstants.Default.YEAR_MONTH.toString();

    public ExpenseRequestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ExpenseRequestBuilder withCategory(ExpenseCategory category) {
        this.category = category;
        return this;
    }

    public ExpenseRequestBuilder withMoney(MoneyRequest money) {
        this.money = money;
        return this;
    }

    public ExpenseRequestBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public ExpenseRequestBuilder withMonth(String month) {
        this.month = month;
        return this;
    }

    public ExpenseRequest buildRequest() {
        ExpenseRequest request = new ExpenseRequest();
        request.setName(name);
        request.setCategory(category);
        request.setMoney(money);
        request.setType(type);
        request.setMonth(month);
        return request;
    }

    public static ExpenseRequestBuilder defaultExpenseRequest() {
        return new ExpenseRequestBuilder();
    }

    public static ExpenseRequestBuilder updatedExpenseRequest() {
        return new ExpenseRequestBuilder()
            .withName(ExpenseConstants.Updated.NAME)
            .withCategory(ExpenseConstants.Updated.CATEGORY)
            .withMoney(MoneyRequestBuilder.updatedMoney().buildRequest())
            .withType(ExpenseConstants.Updated.TYPE)
            .withMonth(ExpenseConstants.Updated.YEAR_MONTH.toString());
    }
}
