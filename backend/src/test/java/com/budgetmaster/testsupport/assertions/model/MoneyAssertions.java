package com.budgetmaster.testsupport.assertions.model;

import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.constants.PathConstants;

import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Currency;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MoneyAssertions {

    private final ResultActions resultActions;
    private final String basePath;

    private MoneyAssertions(ResultActions resultActions, String basePath) {
        this.resultActions = resultActions;
        this.basePath = basePath;
    }

    public static MoneyAssertions assertThat(ResultActions resultActions) {
        return new MoneyAssertions(resultActions, PathConstants.JsonProperties.BASE);
    }

    public static MoneyAssertions assertThat(ResultActions resultActions, String basePath) {
        return new MoneyAssertions(resultActions, basePath);
    }

    public MoneyAssertions hasAmount(BigDecimal expectedAmount) throws Exception {
        resultActions.andExpect(jsonPath(basePath + PathConstants.JsonProperties.Money.AMOUNT).value(expectedAmount.toString()));
        return this;
    }

    public MoneyAssertions hasCurrency(Currency expectedCurrency) throws Exception {
        resultActions.andExpect(jsonPath(basePath + PathConstants.JsonProperties.Money.CURRENCY).value(expectedCurrency.getCurrencyCode()));
        return this;
    }

    public MoneyAssertions hasMoney(Money expectedMoney) throws Exception {
        return hasAmount(expectedMoney.getAmount())
            .hasCurrency(expectedMoney.getCurrency());
    }
} 