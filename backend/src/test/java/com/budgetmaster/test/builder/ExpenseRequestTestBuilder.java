package com.budgetmaster.test.builder;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.enums.ExpenseCategory;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.test.constants.TestData.ExpenseTestConstants;

public class ExpenseRequestTestBuilder {
    private String name = ExpenseTestConstants.Default.NAME;
    private ExpenseCategory category = ExpenseTestConstants.Default.CATEGORY;
    private MoneyRequest money = MoneyRequestTestBuilder.defaultExpense().buildRequest();
    private TransactionType type = ExpenseTestConstants.Default.TYPE;
    private String month = ExpenseTestConstants.Default.YEAR_MONTH.toString();

    public ExpenseRequestTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ExpenseRequestTestBuilder withCategory(ExpenseCategory category) {
        this.category = category;
        return this;
    }

    public ExpenseRequestTestBuilder withMoney(MoneyRequest money) {
        this.money = money;
        return this;
    }

    public ExpenseRequestTestBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public ExpenseRequestTestBuilder withMonth(String month) {
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

    public static ExpenseRequestTestBuilder defaultExpenseRequest() {
        return new ExpenseRequestTestBuilder();
    }

    public static ExpenseRequestTestBuilder updatedExpenseRequest() {
        return new ExpenseRequestTestBuilder()
            .withName(ExpenseTestConstants.Updated.NAME)
            .withCategory(ExpenseTestConstants.Updated.CATEGORY)
            .withMoney(MoneyRequestTestBuilder.updatedMoney().buildRequest())
            .withType(ExpenseTestConstants.Updated.TYPE)
            .withMonth(ExpenseTestConstants.Updated.YEAR_MONTH.toString());
    }

    public static ExpenseRequestTestBuilder invalidExpenseRequest() {
        return new ExpenseRequestTestBuilder()
            .withMoney(MoneyRequestTestBuilder.invalidMoney().buildRequest())
            .withMonth(ExpenseTestConstants.Invalid.YEAR_MONTH);
    }
}
