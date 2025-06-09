package com.budgetmaster.testsupport.assertions.integration.list;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.budgetmaster.application.model.Income;
import com.budgetmaster.testsupport.constants.FieldConstants;

public class IncomeIntegrationListAssertions {

  private final List<Income> actualList;

  public IncomeIntegrationListAssertions(List<Income> actualList) {
    this.actualList = actualList;
  }

  public static IncomeIntegrationListAssertions assertIncomes(List<Income> actualList) {
    assertThat(actualList).isNotNull();
    return new IncomeIntegrationListAssertions(actualList);
  }

  public IncomeIntegrationListAssertions hasSize(int expectedSize) {
    assertThat(actualList).hasSize(expectedSize);
    return this;
  }

  public IncomeIntegrationListAssertions contains(Income... expectedIncomes) {
    assertThat(actualList)
        .usingRecursiveComparison()
        .ignoringFields(FieldConstants.Audit.CREATED_AT, FieldConstants.Audit.LAST_UPDATED_AT)
        .isEqualTo(List.of(expectedIncomes));
    return this;
  }
}
