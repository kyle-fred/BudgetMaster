package com.budgetmaster.testsupport.assertions.model.list;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.budgetmaster.application.model.Income;
import com.budgetmaster.testsupport.assertions.model.IncomeModelAssertions;

public class IncomeListAssertions {

  private final List<Income> actualList;

  public IncomeListAssertions(List<Income> actualList) {
    this.actualList = actualList;
  }

  public static IncomeListAssertions assertIncomes(List<Income> actualList) {
    assertThat(actualList).isNotNull();
    return new IncomeListAssertions(actualList);
  }

  public IncomeListAssertions hasSize(int expectedSize) {
    assertThat(actualList).hasSize(expectedSize);
    return this;
  }

  public IncomeModelAssertions first() {
    return new IncomeModelAssertions(actualList.get(0));
  }
}
