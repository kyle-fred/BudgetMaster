package com.budgetmaster.testsupport.expense.builder;

import java.time.YearMonth;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.expense.enums.ExpenseCategory;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.testsupport.expense.constants.ExpenseConstants;
import com.budgetmaster.testsupport.money.builder.MoneyBuilder;

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
