package com.budgetmaster.testsupport.assertions.controller.list;

import org.springframework.test.web.servlet.ResultActions;

import com.budgetmaster.testsupport.assertions.controller.IncomeControllerAssertions;
import com.budgetmaster.testsupport.constants.PathConstants;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IncomeControllerListAssertions {

    private final ResultActions resultActions;
    private int objectIndex;

    private IncomeControllerListAssertions(ResultActions resultActions) {
        this.resultActions = resultActions;
        this.objectIndex = 0;
    }

    public static IncomeControllerListAssertions assertThat(ResultActions resultActions) {
        return new IncomeControllerListAssertions(resultActions);
    }

    public IncomeControllerListAssertions hasSize(int expectedSize) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.BASE).isArray())
            .andExpect(jsonPath(PathConstants.JsonProperties.LENGTH).value(expectedSize));
        return this;
    }

    public IncomeControllerListAssertions next() throws Exception {
        resultActions.andExpect(jsonPath(nextIncome(objectIndex++)).exists());
        return this;
    }

    public IncomeControllerListAssertions isDefaultIncome() throws Exception {
        IncomeControllerAssertions.assertThat(resultActions, nextIncome(objectIndex - 1))
            .isDefaultIncomeResponse();
        return this;
    }

    public IncomeControllerListAssertions isUpdatedIncome() throws Exception {
        IncomeControllerAssertions.assertThat(resultActions, nextIncome(objectIndex - 1))
            .isUpdatedIncomeResponse();
        return this;
    }

    private String nextIncome(int index) {
        return String.format(PathConstants.JsonProperties.SINGLE_OBJECT, index);
    }
}
