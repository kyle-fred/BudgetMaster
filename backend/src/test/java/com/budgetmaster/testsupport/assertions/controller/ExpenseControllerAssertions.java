package com.budgetmaster.testsupport.assertions.controller;

import com.budgetmaster.application.enums.ExpenseCategory;
import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.assertions.controller.error.ErrorControllerAssertions;
import com.budgetmaster.testsupport.builder.model.MoneyBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.PathConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;

public class ExpenseControllerAssertions {

    private final ResultActions resultActions;
    private final String basePath;

    private ExpenseControllerAssertions(ResultActions resultActions, String basePath) {
        this.resultActions = resultActions;
        this.basePath = basePath;
    }

    public static ExpenseControllerAssertions assertThat(ResultActions resultActions) {
        return new ExpenseControllerAssertions(resultActions, PathConstants.JsonProperties.BASE);
    }

    public static ExpenseControllerAssertions assertThat(ResultActions resultActions, String basePath) {
        return new ExpenseControllerAssertions(resultActions, basePath);
    }

    public ExpenseControllerAssertions isOk() throws Exception {
        resultActions.andExpect(status().isOk());
        return this;
    }

    public ExpenseControllerAssertions isNoContent() throws Exception {
        resultActions.andExpect(status().isNoContent());
        return this;
    }

    public ExpenseControllerAssertions hasName(String expectedName) throws Exception {
        resultActions.andExpect(jsonPath(basePath + PathConstants.JsonProperties.Expense.NAME).value(expectedName));
        return this;
    }

    public ExpenseControllerAssertions hasMoney(Money expectedMoney) throws Exception {
        MoneyControllerAssertions.assertThat(resultActions, basePath + PathConstants.JsonProperties.Expense.MONEY)
            .hasMoney(expectedMoney);
        return this;
    }

    public ExpenseControllerAssertions hasCategory(ExpenseCategory expectedCategory) throws Exception {
        resultActions.andExpect(jsonPath(basePath + PathConstants.JsonProperties.Expense.CATEGORY).value(expectedCategory.toString()));
        return this;
    }

    public ExpenseControllerAssertions hasType(TransactionType expectedType) throws Exception {
        resultActions.andExpect(jsonPath(basePath + PathConstants.JsonProperties.Expense.TYPE).value(expectedType.toString()));
        return this;
    }

    public ExpenseControllerAssertions hasMonth(YearMonth expectedMonth) throws Exception {
        resultActions
            .andExpect(jsonPath(basePath + PathConstants.JsonProperties.Expense.MONTH_YEAR).isArray())
            .andExpect(jsonPath(basePath + PathConstants.JsonProperties.Expense.YEAR).value(expectedMonth.getYear()))
            .andExpect(jsonPath(basePath + PathConstants.JsonProperties.Expense.MONTH).value(expectedMonth.getMonthValue()));
        return this;
    }

    public ExpenseControllerAssertions isDefaultExpenseResponse() throws Exception {
        return isOk()
            .hasName(ExpenseConstants.Default.NAME)
            .hasMoney(MoneyBuilder.defaultExpense().build())
            .hasCategory(ExpenseConstants.Default.CATEGORY)
            .hasType(ExpenseConstants.Default.TYPE)
            .hasMonth(ExpenseConstants.Default.YEAR_MONTH);
    }

    public ExpenseControllerAssertions isUpdatedExpenseResponse() throws Exception {
        return isOk()
            .hasName(ExpenseConstants.Updated.NAME)
            .hasMoney(MoneyBuilder.updatedMoney().build())
            .hasCategory(ExpenseConstants.Updated.CATEGORY)
            .hasType(ExpenseConstants.Updated.TYPE)
            .hasMonth(ExpenseConstants.Updated.YEAR_MONTH);
    }

    public ErrorControllerAssertions isNotFoundForMonth(YearMonth month) throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isNotFoundResponse(
                String.format(ErrorConstants.Expense.NOT_FOUND_BY_MONTH, month),
                PathConstants.Error.Expense.URI
            );
    }

    public ErrorControllerAssertions isNotFoundForId(Long id) throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isNotFoundResponse(
                String.format(ErrorConstants.Expense.NOT_FOUND_WITH_ID, id),
                String.format(PathConstants.Error.Expense.URI_WITH_ID, id)
            );
    }

    public ErrorControllerAssertions isInternalServerError() throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isInternalServerErrorResponse(PathConstants.Error.Expense.URI);
    }

    public ErrorControllerAssertions isConflict() throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isConflictResponse(PathConstants.Error.Expense.URI);
    }
}
