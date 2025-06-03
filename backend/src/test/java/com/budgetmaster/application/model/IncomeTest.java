package com.budgetmaster.application.model;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.testsupport.assertions.model.IncomeModelAssertions;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

public class IncomeTest {

    private Income income;
    private IncomeRequest defaultIncomeRequest;

    @BeforeEach
    void setUp() {
        income = new Income();
        defaultIncomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .buildRequest();
    }
    
    @Test
    void from_ValidRequest_ReturnsIncome() {
        income = Income.from(defaultIncomeRequest);

        IncomeModelAssertions.assertIncome(income)
            .isDefaultIncome();
    }
    
    @Test
    void updateFrom_ValidRequest_UpdatesIncome() {
        income.updateFrom(defaultIncomeRequest);

        IncomeModelAssertions.assertIncome(income)
            .isDefaultIncome();
    }

    @Test
    void deepCopy_ReturnsNewIncomeWithSameValues() {
        income = IncomeBuilder.defaultIncome().build();
        Income copy = income.deepCopy();

        IncomeModelAssertions.assertIncome(copy)
            .isDefaultIncome();
    }

    @Test
    void of_WithValidParameters_CreatesIncomeWithCorrectValues() {
        income = IncomeBuilder.defaultIncome().build();

        IncomeModelAssertions.assertIncome(income)
            .isDefaultIncome();
    }
}
