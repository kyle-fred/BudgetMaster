package com.budgetmaster.testsupport.assertions.integration;

import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.model.Money;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.testsupport.builder.model.MoneyBuilder;
import com.budgetmaster.testsupport.constants.FieldConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;

public class IncomeIntegrationAssertions {
    
    private final Income actual;

    public IncomeIntegrationAssertions(Income actual) {
        this.actual = actual;
    }

    public static IncomeIntegrationAssertions assertIncome(Income actual) {
        assertThat(actual).isNotNull();
        return new IncomeIntegrationAssertions(actual);
    }

    public static void assertIncomeDeleted(Income income, IncomeRepository incomeRepository) {
        assertThat(incomeRepository.findById(income.getId())).isEmpty();
    }

    public IncomeIntegrationAssertions hasId() {
        assertThat(actual.getId()).isNotNull();
        return this;
    }

    public IncomeIntegrationAssertions hasName(String expectedName) {
        assertThat(actual.getName()).isEqualTo(expectedName);
        return this;
    }

    public IncomeIntegrationAssertions hasSource(String expectedSource) {
        assertThat(actual.getSource()).isEqualTo(expectedSource);
        return this;
    }

    public IncomeIntegrationAssertions hasMoney(Money expectedMoney) {
        assertThat(actual.getMoney()).usingRecursiveComparison().isEqualTo(expectedMoney);
        return this;
    }

    public IncomeIntegrationAssertions hasType(TransactionType expectedType) {
        assertThat(actual.getType()).isEqualTo(expectedType);
        return this;
    }

    public IncomeIntegrationAssertions hasMonth(YearMonth expectedMonth) {
        assertThat(actual.getMonth()).isEqualTo(expectedMonth);
        return this;
    }

    public IncomeIntegrationAssertions hasCreatedAt() {
        assertThat(actual.getCreatedAt()).isNotNull();
        return this;
    }

    public IncomeIntegrationAssertions hasUpdatedAt() {
        assertThat(actual.getLastUpdatedAt()).isNotNull();
        return this;
    }

    public IncomeIntegrationAssertions isDefaultIncome() {
        return hasId()
            .hasName(IncomeConstants.Default.NAME)
            .hasSource(IncomeConstants.Default.SOURCE)
            .hasMoney(MoneyBuilder.defaultIncome().build())
            .hasType(IncomeConstants.Default.TYPE)
            .hasMonth(IncomeConstants.Default.YEAR_MONTH)
            .hasCreatedAt()
            .hasUpdatedAt();
    }

    public IncomeIntegrationAssertions isEqualTo(Income expected) {
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields(FieldConstants.Audit.CREATED_AT, FieldConstants.Audit.LAST_UPDATED_AT)
            .isEqualTo(expected);
        return this;
    }
}
