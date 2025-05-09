package com.budgetmaster.test.factory;

import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData.ExpenseTestConstants;

public final class ExpenseTestFactory {
    private ExpenseTestFactory() {}

    public static Expense createDefaultExpense() {
        Expense expense = Expense.of(
            ExpenseTestConstants.Default.NAME,
            Money.of(ExpenseTestConstants.Default.AMOUNT, ExpenseTestConstants.Default.CURRENCY),
            ExpenseTestConstants.Default.CATEGORY,
            ExpenseTestConstants.Default.TYPE,
            ExpenseTestConstants.Default.YEAR_MONTH
        );
        expense.setId(ExpenseTestConstants.Default.ID);
        return expense;
    }

    public static ExpenseRequest createDefaultExpenseRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(ExpenseTestConstants.Default.AMOUNT);
        moneyRequest.setCurrency(ExpenseTestConstants.Default.CURRENCY);

        ExpenseRequest request = new ExpenseRequest();
        request.setName(ExpenseTestConstants.Default.NAME);
        request.setCategory(ExpenseTestConstants.Default.CATEGORY);
        request.setMoney(moneyRequest);
        request.setType(ExpenseTestConstants.Default.TYPE);
        request.setMonth(ExpenseTestConstants.Default.YEAR_MONTH.toString());
        return request;
    }

    public static ExpenseRequest createUpdatedExpenseRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(ExpenseTestConstants.Updated.AMOUNT);
        moneyRequest.setCurrency(ExpenseTestConstants.Default.CURRENCY);

        ExpenseRequest request = new ExpenseRequest();
        request.setName(ExpenseTestConstants.Updated.NAME);
        request.setCategory(ExpenseTestConstants.Updated.CATEGORY);
        request.setMoney(moneyRequest);
        request.setType(ExpenseTestConstants.Updated.TYPE);
        request.setMonth(ExpenseTestConstants.Updated.YEAR_MONTH.toString());
        return request;
    }

    public static ExpenseRequest createInvalidExpenseRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(ExpenseTestConstants.Invalid.AMOUNT);
        moneyRequest.setCurrency(ExpenseTestConstants.Default.CURRENCY);

        ExpenseRequest request = new ExpenseRequest();
        request.setMoney(moneyRequest);
        request.setType(ExpenseTestConstants.Default.TYPE);
        request.setMonth(ExpenseTestConstants.Invalid.YEAR_MONTH.toString());
        return request;
    }
}
