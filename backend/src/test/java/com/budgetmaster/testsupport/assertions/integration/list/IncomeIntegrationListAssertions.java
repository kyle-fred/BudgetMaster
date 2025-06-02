package com.budgetmaster.testsupport.assertions.integration.list;

import java.util.List;

import com.budgetmaster.application.model.Income;
import com.budgetmaster.testsupport.assertions.integration.IncomeIntegrationAssertions;

import static org.assertj.core.api.Assertions.assertThat;

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

    public IncomeIntegrationAssertions first() {
        return new IncomeIntegrationAssertions(actualList.get(0));
    }
    
}
