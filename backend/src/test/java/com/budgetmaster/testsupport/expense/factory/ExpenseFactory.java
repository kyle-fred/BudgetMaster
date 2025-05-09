package com.budgetmaster.testsupport.expense.factory;

import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.model.Expense;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.testsupport.expense.constants.ExpenseConstants;

public final class ExpenseFactory {
    private ExpenseFactory() {}

    public static Expense createDefaultExpense() {
        Expense expense = Expense.of(
            ExpenseConstants.Default.NAME,
            Money.of(ExpenseConstants.Default.AMOUNT, ExpenseConstants.Default.CURRENCY),
            ExpenseConstants.Default.CATEGORY,
            ExpenseConstants.Default.TYPE,
            ExpenseConstants.Default.YEAR_MONTH
        );
        expense.setId(ExpenseConstants.Default.ID);
        return expense;
    }

    public static ExpenseRequest createDefaultExpenseRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(ExpenseConstants.Default.AMOUNT);
        moneyRequest.setCurrency(ExpenseConstants.Default.CURRENCY);

        ExpenseRequest request = new ExpenseRequest();
        request.setName(ExpenseConstants.Default.NAME);
        request.setCategory(ExpenseConstants.Default.CATEGORY);
        request.setMoney(moneyRequest);
        request.setType(ExpenseConstants.Default.TYPE);
        request.setMonth(ExpenseConstants.Default.YEAR_MONTH.toString());
        return request;
    }

    public static ExpenseRequest createUpdatedExpenseRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(ExpenseConstants.Updated.AMOUNT);
        moneyRequest.setCurrency(ExpenseConstants.Default.CURRENCY);

        ExpenseRequest request = new ExpenseRequest();
        request.setName(ExpenseConstants.Updated.NAME);
        request.setCategory(ExpenseConstants.Updated.CATEGORY);
        request.setMoney(moneyRequest);
        request.setType(ExpenseConstants.Updated.TYPE);
        request.setMonth(ExpenseConstants.Updated.YEAR_MONTH.toString());
        return request;
    }

    public static ExpenseRequest createInvalidExpenseRequest() {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(ExpenseConstants.Invalid.AMOUNT);
        moneyRequest.setCurrency(ExpenseConstants.Default.CURRENCY);

        ExpenseRequest request = new ExpenseRequest();
        request.setMoney(moneyRequest);
        request.setType(ExpenseConstants.Default.TYPE);
        request.setMonth(ExpenseConstants.Invalid.YEAR_MONTH.toString());
        return request;
    }
}
