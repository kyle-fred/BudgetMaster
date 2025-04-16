package com.budgetmaster.utils.model;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.model.Income;
import com.budgetmaster.model.value.Money;
import com.budgetmaster.model.Expense;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.utils.string.StringUtils;

public class FinancialModelUtils {
    /**
     * Builds the Income object from IncomeRequest.
     */
    public static Income buildIncome(IncomeRequest request) {
        return new Income(
                StringUtils.capitalize(request.getName()),
                StringUtils.capitalize(request.getSource()),
                Money.of(request.getMoney().getAmount(), request.getMoney().getCurrency()),
                request.getType(),
                DateUtils.getValidYearMonth(request.getMonth())
        );
    }

    /**
     * Modifies an existing Income object with values from IncomeRequest.
     */
    public static void modifyIncome(Income income, IncomeRequest request) {
        income.setName(StringUtils.capitalize(request.getName()));
        income.setSource(StringUtils.capitalize(request.getSource()));
        income.setMoney(Money.of(request.getMoney().getAmount(), request.getMoney().getCurrency()));
        income.setType(request.getType());
        if (request.getMonth() != null && !request.getMonth().isEmpty()) {
            income.setMonth(DateUtils.getValidYearMonth(request.getMonth()));
        }
    }

    /**
     * Builds the Expense object from ExpenseRequest.
     */
    public static Expense buildExpense(ExpenseRequest request) {
        return new Expense(
                StringUtils.capitalize(request.getName()),
                Money.of(request.getMoney().getAmount(), request.getMoney().getCurrency()),
                request.getCategory(),
                request.getType(),
                DateUtils.getValidYearMonth(request.getMonth())
        );
    }

    /**
     * Modifies an existing Expense object with values from ExpenseRequest.
     */
    public static void modifyExpense(Expense expense, ExpenseRequest request) {
        expense.setName(StringUtils.capitalize(request.getName()));
        expense.setMoney(Money.of(request.getMoney().getAmount(), request.getMoney().getCurrency()));
        expense.setCategory(request.getCategory());
        expense.setType(request.getType());
        if (request.getMonth() != null && !request.getMonth().isEmpty()) {
            expense.setMonth(DateUtils.getValidYearMonth(request.getMonth()));
        }
    }
}
