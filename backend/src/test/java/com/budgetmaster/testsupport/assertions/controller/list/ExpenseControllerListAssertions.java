package com.budgetmaster.testsupport.assertions.controller.list;

import org.springframework.test.web.servlet.ResultActions;

import com.budgetmaster.testsupport.assertions.controller.ExpenseControllerAssertions;
import com.budgetmaster.testsupport.constants.PathConstants;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExpenseControllerListAssertions {

    private final ResultActions resultActions;
    private int objectIndex;

    private ExpenseControllerListAssertions(ResultActions resultActions) {
        this.resultActions = resultActions;
        this.objectIndex = 0;
    }

    public static ExpenseControllerListAssertions assertThat(ResultActions resultActions) {
        return new ExpenseControllerListAssertions(resultActions);
    }

    public ExpenseControllerListAssertions hasSize(int expectedSize) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.BASE).isArray())
            .andExpect(jsonPath(PathConstants.JsonProperties.LENGTH).value(expectedSize));
        return this;
    }

    public ExpenseControllerListAssertions next() throws Exception {
        resultActions.andExpect(jsonPath(nextExpense(objectIndex++)).exists());
        return this;
    }

    public ExpenseControllerListAssertions isDefaultExpense() throws Exception {
        ExpenseControllerAssertions.assertThat(resultActions, nextExpense(objectIndex - 1))
            .isDefaultExpenseResponse();
        return this;
    }

    public ExpenseControllerListAssertions isUpdatedExpense() throws Exception {
        ExpenseControllerAssertions.assertThat(resultActions, nextExpense(objectIndex - 1))
            .isUpdatedExpenseResponse();
        return this;
    }

    private String nextExpense(int index) {
        return String.format(PathConstants.JsonProperties.SINGLE_OBJECT, index);
    }
}