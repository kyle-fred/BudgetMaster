package com.budgetmaster.utils.model;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.model.Budget;
import com.budgetmaster.model.Income;
import com.budgetmaster.model.Expense;
import com.budgetmaster.utils.date.DateUtils;

public class FinancialModelUtils {

    /**
     * Builds the BudgetRequest by setting the proper monthYear format.
     */
    public static BudgetRequest buildBudgetRequest(BudgetRequest request) {
        request.setMonthYear(DateUtils.getValidYearMonth(request.getMonthYear()).toString());
        return request;
    }

    /**
     * Builds the Budget object from BudgetRequest.
     */
    public static Budget buildBudget(BudgetRequest request) {
        return new Budget(
                request.getIncome(),
                request.getExpenses(),
                DateUtils.getValidYearMonth(request.getMonthYear())
        );
    }

    /**
     * Modifies an existing Budget object with values from BudgetRequest.
     */
    public static void modifyBudget(Budget budget, BudgetRequest request) {
        budget.setIncome(request.getIncome());
        budget.setExpenses(request.getExpenses());
        budget.setSavings(request.getIncome() - request.getExpenses());

        if (request.getMonthYear() != null && !request.getMonthYear().isEmpty()) {
            budget.setMonthYear(DateUtils.getValidYearMonth(request.getMonthYear()));
        }
    }

    /**
     * Builds the IncomeRequest by setting the proper monthYear format.
     */
    public static IncomeRequest buildIncomeRequest(IncomeRequest request) {
        request.setMonthYear(DateUtils.getValidYearMonth(request.getMonthYear()).toString());
        return request;
    }

    /**
     * Builds the Income object from IncomeRequest.
     */
    public static Income buildIncome(IncomeRequest request) {
        return new Income(
                request.getName(),
                request.getSource(),
                request.getAmount(),
                request.getType(),
                DateUtils.getValidYearMonth(request.getMonthYear())
        );
    }

    /**
     * Modifies an existing Income object with values from IncomeRequest.
     */
    public static void modifyIncome(Income income, IncomeRequest request) {
        income.setName(request.getName());
        income.setSource(request.getSource());
        income.setAmount(request.getAmount());
        income.setType(request.getType());

        if (request.getMonthYear() != null && !request.getMonthYear().isEmpty()) {
            income.setMonthYear(DateUtils.getValidYearMonth(request.getMonthYear()));
        }
    }

    /**
     * Builds the ExpenseRequest by setting the proper monthYear format.
     */
    public static ExpenseRequest buildExpenseRequest(ExpenseRequest request) {
        request.setMonthYear(DateUtils.getValidYearMonth(request.getMonthYear()).toString());
        return request;
    }

    /**
     * Builds the Expense object from ExpenseRequest.
     */
    public static Expense buildExpense(ExpenseRequest request) {
        return new Expense(
                request.getName(),
                request.getAmount(),
                request.getCategory(),
                request.getType(),
                DateUtils.getValidYearMonth(request.getMonthYear())
        );
    }

    /**
     * Modifies an existing Expense object with values from ExpenseRequest.
     */
    public static void modifyExpense(Expense expense, ExpenseRequest request) {
        expense.setName(request.getName());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setType(request.getType());

        if (request.getMonthYear() != null && !request.getMonthYear().isEmpty()) {
            expense.setMonthYear(DateUtils.getValidYearMonth(request.getMonthYear()));
        }
    }
}
