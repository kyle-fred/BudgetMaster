package com.budgetmaster.testsupport.assertions.controller;

import com.budgetmaster.testsupport.assertions.controller.error.ErrorControllerAssertions;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.PathConstants;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

public class BudgetControllerAssertions {

    private final ResultActions resultActions;

    private BudgetControllerAssertions(ResultActions resultActions) {
        this.resultActions = resultActions;
    }

    public static BudgetControllerAssertions assertThat(ResultActions resultActions) {
        return new BudgetControllerAssertions(resultActions);
    }

    public BudgetControllerAssertions isOk() throws Exception {
        resultActions.andExpect(status().isOk());
        return this;
    }

    public BudgetControllerAssertions isNoContent() throws Exception {
        resultActions.andExpect(status().isNoContent());
        return this;
    }

    public BudgetControllerAssertions hasTotalIncome(BigDecimal expectedTotalIncome) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.TOTAL_INCOME).value(expectedTotalIncome.toString()));
        return this;
    }

    public BudgetControllerAssertions hasTotalExpense(BigDecimal expectedTotalExpense) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.TOTAL_EXPENSE).value(expectedTotalExpense.toString()));
        return this;
    }

    public BudgetControllerAssertions hasSavings(BigDecimal expectedSavings) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.SAVINGS).value(expectedSavings.toString()));
        return this;
    }

    public BudgetControllerAssertions hasCurrency(Currency expectedCurrency) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.Money.CURRENCY).value(expectedCurrency.getCurrencyCode()));
        return this;
    }

    public BudgetControllerAssertions hasMonth(YearMonth expectedMonth) throws Exception {
        resultActions
            .andExpect(jsonPath(PathConstants.JsonProperties.MONTH_YEAR).isArray())
            .andExpect(jsonPath(PathConstants.JsonProperties.Month.YEAR_VALUE).value(expectedMonth.getYear()))
            .andExpect(jsonPath(PathConstants.JsonProperties.Month.MONTH_VALUE).value(expectedMonth.getMonthValue()));
        return this;
    }

    public BudgetControllerAssertions isDefaultBudgetResponse() throws Exception {
        return isOk()
            .hasTotalIncome(BudgetConstants.Default.TOTAL_INCOME)
            .hasTotalExpense(BudgetConstants.Default.TOTAL_EXPENSE)
            .hasSavings(BudgetConstants.Default.SAVINGS)
            .hasCurrency(BudgetConstants.Default.CURRENCY)
            .hasMonth(BudgetConstants.Default.YEAR_MONTH);
    }

    public ErrorControllerAssertions isNotFoundForMonth(YearMonth month) throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isNotFoundResponse(
                String.format(ErrorConstants.Budget.NOT_FOUND_FOR_MONTH, month),
                PathConstants.Error.Budget.URI
            );
    }

    public ErrorControllerAssertions isNotFoundForId(Long id) throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isNotFoundResponse(
                String.format(ErrorConstants.Budget.NOT_FOUND_WITH_ID, id),
                String.format(PathConstants.Error.Budget.URI_WITH_ID, id)
            );
    }

    public ErrorControllerAssertions isInternalServerError() throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isInternalServerErrorResponse(PathConstants.Error.Budget.URI);
    }
} 