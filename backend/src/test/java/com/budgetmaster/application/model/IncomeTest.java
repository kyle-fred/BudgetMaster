package com.budgetmaster.application.model;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.testsupport.assertions.model.IncomeModelAssertions;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Income Model Tests")
class IncomeTest {

    private Income income;
    private IncomeRequest defaultIncomeRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();

    @BeforeEach
    void setUp() {
        income = new Income();
    }

    @Nested
    @DisplayName("Income Creation")
    class IncomeCreation {
        
        @Test
        @DisplayName("Should create income from valid request")
        void from_withValidRequest_returnsIncome() {
            income = Income.from(defaultIncomeRequest);

            IncomeModelAssertions.assertIncome(income)
                .isDefaultIncome();
        }

        @Test
        @DisplayName("Should create income with valid parameters")
        void of_withValidParameters_createsIncomeWithCorrectValues() {
            income = IncomeBuilder.defaultIncome().build();

            IncomeModelAssertions.assertIncome(income)
                .isDefaultIncome();
        }
    }

    @Nested
    @DisplayName("Income Operations")
    class IncomeOperations {
        
        @Test
        @DisplayName("Should update income from valid request")
        void updateFrom_withValidRequest_updatesIncome() {
            income.updateFrom(defaultIncomeRequest);

            IncomeModelAssertions.assertIncome(income)
                .isDefaultIncome();
        }

        @Test
        @DisplayName("Should create deep copy with same values")
        void deepCopy_returnsNewIncomeWithSameValues() {
            income = IncomeBuilder.defaultIncome().build();
            Income copy = income.deepCopy();

            IncomeModelAssertions.assertIncome(copy)
                .isDefaultIncome();
        }
    }
}
