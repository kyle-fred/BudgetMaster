package com.budgetmaster.testsupport.builder;

import java.time.YearMonth;

import com.budgetmaster.application.enums.ExpenseCategory;
import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.application.model.Expense;
import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

public class ExpenseBuilder {
    private Long id = ExpenseConstants.Default.ID;
    private String name = ExpenseConstants.Default.NAME;
    private ExpenseCategory category = ExpenseConstants.Default.CATEGORY;
    private Money money = MoneyBuilder.defaultExpense().build();
    private TransactionType type = ExpenseConstants.Default.TYPE;
    private YearMonth month = ExpenseConstants.Default.YEAR_MONTH;

    public ExpenseBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ExpenseBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ExpenseBuilder withCategory(ExpenseCategory category) {
        this.category = category;
        return this;
    }

    public ExpenseBuilder withMoney(Money money) {
        this.money = money;
        return this;
    }

    public ExpenseBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public ExpenseBuilder withMonth(YearMonth month) {
        this.month = month;
        return this;
    }

    public Expense build() {
        Expense expense = ExpenseFactory.createDefaultExpense();
        expense.setName(name);
        expense.setCategory(category);
        expense.setMoney(money);
        expense.setType(type);
        expense.setMonth(month);
        expense.setId(id);
        return expense;
    }

    public static ExpenseBuilder defaultExpense() {
        return new ExpenseBuilder();
    }

    public static ExpenseBuilder updatedExpense() {
        return new ExpenseBuilder()
            .withName(ExpenseConstants.Updated.NAME)
            .withCategory(ExpenseConstants.Updated.CATEGORY)
            .withMoney(MoneyBuilder.updatedMoney().build())
            .withType(ExpenseConstants.Updated.TYPE)
            .withMonth(ExpenseConstants.Updated.YEAR_MONTH);
    }
}
