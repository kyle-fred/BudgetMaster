package com.budgetmaster.testsupport.assertions.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;

import org.springframework.test.web.servlet.ResultActions;

import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.assertions.controller.error.ErrorControllerAssertions;
import com.budgetmaster.testsupport.builder.model.MoneyBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.PathConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

public class IncomeControllerAssertions {

    private final ResultActions resultActions;
    private final String basePath;

    private IncomeControllerAssertions(ResultActions resultActions, String basePath) {
        this.resultActions = resultActions;
        this.basePath = basePath;
    }

    public static IncomeControllerAssertions assertThat(ResultActions resultActions) {
        return new IncomeControllerAssertions(resultActions, PathConstants.JsonProperties.BASE);
    }

    public static IncomeControllerAssertions assertThat(ResultActions resultActions, String basePath) {
        return new IncomeControllerAssertions(resultActions, basePath);
    }

    public IncomeControllerAssertions isOk() throws Exception {
        resultActions.andExpect(status().isOk());
        return this;
    }

    public IncomeControllerAssertions isNoContent() throws Exception {
        resultActions.andExpect(status().isNoContent());
        return this;
    }

    public IncomeControllerAssertions hasName(String expectedName) throws Exception {
        resultActions.andExpect(jsonPath(basePath + PathConstants.JsonProperties.NAME).value(expectedName));
        return this;
    }

    public IncomeControllerAssertions hasSource(String expectedSource) throws Exception {
        resultActions.andExpect(jsonPath(basePath + PathConstants.JsonProperties.SOURCE).value(expectedSource));
        return this;
    }

    public IncomeControllerAssertions hasMoney(Money expectedMoney) throws Exception {
        MoneyControllerAssertions.assertThat(resultActions, basePath + PathConstants.JsonProperties.MONEY)
            .hasMoney(expectedMoney);
        return this;
    }

    public IncomeControllerAssertions hasType(TransactionType expectedType) throws Exception {
        resultActions.andExpect(jsonPath(basePath + PathConstants.JsonProperties.TYPE).value(expectedType.toString()));
        return this;
    }

    public IncomeControllerAssertions hasMonth(YearMonth expectedMonth) throws Exception {
        resultActions
            .andExpect(jsonPath(basePath + PathConstants.JsonProperties.MONTH_YEAR).isArray())
            .andExpect(jsonPath(basePath + PathConstants.JsonProperties.Month.YEAR_VALUE).value(expectedMonth.getYear()))
            .andExpect(jsonPath(basePath + PathConstants.JsonProperties.Month.MONTH_VALUE).value(expectedMonth.getMonthValue()));
        return this;
    }

    public IncomeControllerAssertions isDefaultIncomeResponse() throws Exception {
        return isOk()
            .hasName(IncomeConstants.Default.NAME)
            .hasSource(IncomeConstants.Default.SOURCE)
            .hasMoney(MoneyBuilder.defaultIncome().build())
            .hasType(IncomeConstants.Default.TYPE)
            .hasMonth(IncomeConstants.Default.YEAR_MONTH);
    }

    public IncomeControllerAssertions isUpdatedIncomeResponse() throws Exception {
        return isOk()
            .hasName(IncomeConstants.Updated.NAME)
            .hasSource(IncomeConstants.Updated.SOURCE)
            .hasMoney(MoneyBuilder.updatedMoney().build())
            .hasType(IncomeConstants.Updated.TYPE)
            .hasMonth(IncomeConstants.Updated.YEAR_MONTH);
    }

    public ErrorControllerAssertions isNotFoundForMonth(YearMonth month) throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isNotFoundResponse(
                String.format(ErrorConstants.Income.NOT_FOUND_FOR_MONTH, month),
                PathConstants.Error.Income.URI
            );
    }

    public ErrorControllerAssertions isNotFoundForId(Long id) throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isNotFoundResponse(
                String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, id),
                String.format(PathConstants.Error.Income.URI_WITH_ID, id)
            );
    }

    public ErrorControllerAssertions isInternalServerError() throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isInternalServerErrorResponse(PathConstants.Error.Income.URI);
    }

    public ErrorControllerAssertions isConflict() throws Exception {
        return ErrorControllerAssertions.assertThat(resultActions)
            .isConflictResponse(PathConstants.Error.Income.URI);
    }
}
