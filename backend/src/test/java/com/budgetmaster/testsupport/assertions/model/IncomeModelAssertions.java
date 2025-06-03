package com.budgetmaster.testsupport.assertions.model;

import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.builder.model.MoneyBuilder;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

public class IncomeModelAssertions {

    private final Income actual;

    public IncomeModelAssertions(Income actual) {
        this.actual = actual;
    }

    public static IncomeModelAssertions assertIncome(Income actual) {
        assertNotNull(actual);
        return new IncomeModelAssertions(actual);
    }

    public IncomeModelAssertions hasName(String expectedName) {
        assertEquals(expectedName, actual.getName());
        return this;
    }

    public IncomeModelAssertions hasSource(String expectedSource) {
        assertEquals(expectedSource, actual.getSource());
        return this;
    }

    public IncomeModelAssertions hasMoney(Money expectedMoney) {
        assertEquals(expectedMoney, actual.getMoney());
        return this;
    }

    public IncomeModelAssertions hasType(TransactionType expectedType) {
        assertEquals(expectedType, actual.getType());
        return this;
    }

    public IncomeModelAssertions hasMonth(YearMonth expectedMonth) {
        assertEquals(expectedMonth, actual.getMonth());
        return this;
    }

    public IncomeModelAssertions isDefaultIncome() {
        return hasName(IncomeConstants.Default.NAME)
            .hasSource(IncomeConstants.Default.SOURCE)
            .hasMoney(MoneyBuilder.defaultIncome().build())
            .hasType(IncomeConstants.Default.TYPE)
            .hasMonth(IncomeConstants.Default.YEAR_MONTH);
    }

    public IncomeModelAssertions isUpdatedIncome() {
        return hasName(IncomeConstants.Updated.NAME)
            .hasSource(IncomeConstants.Updated.SOURCE)
            .hasMoney(MoneyBuilder.updatedMoney().build())
            .hasType(IncomeConstants.Updated.TYPE)
            .hasMonth(IncomeConstants.Updated.YEAR_MONTH);
    }
}
