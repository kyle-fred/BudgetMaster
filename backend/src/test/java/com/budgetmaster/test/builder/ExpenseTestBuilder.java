package com.budgetmaster.test.builder;

import java.time.YearMonth;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.expense.enums.ExpenseCategory;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData.ExpenseTestConstants;

public class ExpenseTestBuilder {
    private Long id = ExpenseTestConstants.Default.ID;
    private String name = ExpenseTestConstants.Default.NAME;
    private ExpenseCategory category = ExpenseTestConstants.Default.CATEGORY;
    private Money money = MoneyTestBuilder.defaultExpense().build();
    private TransactionType type = ExpenseTestConstants.Default.TYPE;
    private YearMonth month = ExpenseTestConstants.Default.YEAR_MONTH;

    public ExpenseTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ExpenseTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ExpenseTestBuilder withCategory(ExpenseCategory category) {
        this.category = category;
        return this;
    }

    public ExpenseTestBuilder withMoney(Money money) {
        this.money = money;
        return this;
    }

    public ExpenseTestBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public ExpenseTestBuilder withMonth(YearMonth month) {
        this.month = month;
        return this;
    }

    public Expense build() {
        Expense expense = Expense.of(
            name,
            money,
            category,
            type,
            month
        );
        expense.setId(id);
        return expense;
    }

    public static ExpenseTestBuilder defaultExpense() {
        return new ExpenseTestBuilder();
    }

    public static ExpenseTestBuilder updatedExpense() {
        return new ExpenseTestBuilder()
            .withName(ExpenseTestConstants.Updated.NAME)
            .withCategory(ExpenseTestConstants.Updated.CATEGORY)
            .withMoney(MoneyTestBuilder.updatedMoney().build())
            .withType(ExpenseTestConstants.Updated.TYPE)
            .withMonth(ExpenseTestConstants.Updated.YEAR_MONTH);
    }

    public static ExpenseTestBuilder invalidExpense() {
        return new ExpenseTestBuilder()
            .withMoney(MoneyTestBuilder.invalidMoney().build())
            .withMonth(YearMonth.parse(ExpenseTestConstants.Invalid.YEAR_MONTH));
    }
}
