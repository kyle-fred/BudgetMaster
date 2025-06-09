package com.budgetmaster.testsupport.assertions.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Currency;

import org.springframework.test.web.servlet.ResultActions;

import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.constants.PathConstants;

public class MoneyControllerAssertions {

  private final ResultActions resultActions;
  private final String basePath;

  private MoneyControllerAssertions(ResultActions resultActions, String basePath) {
    this.resultActions = resultActions;
    this.basePath = basePath;
  }

  public static MoneyControllerAssertions assertThat(ResultActions resultActions) {
    return new MoneyControllerAssertions(resultActions, PathConstants.JsonProperties.BASE);
  }

  public static MoneyControllerAssertions assertThat(ResultActions resultActions, String basePath) {
    return new MoneyControllerAssertions(resultActions, basePath);
  }

  public MoneyControllerAssertions hasAmount(BigDecimal expectedAmount) throws Exception {
    resultActions.andExpect(
        jsonPath(basePath + PathConstants.JsonProperties.Money.AMOUNT)
            .value(expectedAmount.toString()));
    return this;
  }

  public MoneyControllerAssertions hasCurrency(Currency expectedCurrency) throws Exception {
    resultActions.andExpect(
        jsonPath(basePath + PathConstants.JsonProperties.Money.CURRENCY)
            .value(expectedCurrency.getCurrencyCode()));
    return this;
  }

  public MoneyControllerAssertions hasMoney(Money expectedMoney) throws Exception {
    return hasAmount(expectedMoney.getAmount()).hasCurrency(expectedMoney.getCurrency());
  }
}
