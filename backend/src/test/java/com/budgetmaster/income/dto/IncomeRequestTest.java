package com.budgetmaster.income.dto;

import java.util.Set;

import com.budgetmaster.test.builder.IncomeTestBuilder;
import com.budgetmaster.test.constants.IncomeTestConstants;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.factory.IncomeTestFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IncomeRequestTest {    
    // -- Dependencies --
    private static Validator validator;

    // -- Setup --
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidIncomeRequest() {
        IncomeRequest incomeRequest = IncomeTestFactory.createDefaultIncomeRequest();
        
        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullName() {
        IncomeRequest incomeRequest = IncomeTestBuilder.defaultIncome()
            .withName(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.IncomeErrorMessageConstants.INCOME_NAME_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullSource() {
        IncomeRequest incomeRequest = IncomeTestBuilder.defaultIncome()
            .withSource(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.IncomeErrorMessageConstants.INCOME_SOURCE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMoney() {
        IncomeRequest incomeRequest = IncomeTestFactory.createDefaultIncomeRequest();
        incomeRequest.setMoney(null);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_DETAILS_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullType() {
        IncomeRequest incomeRequest = IncomeTestBuilder.defaultIncome()
            .withType(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.IncomeErrorMessageConstants.INCOME_TYPE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMonth() {
        IncomeRequest incomeRequest = IncomeTestBuilder.defaultIncome()
            .withNullMonth(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonth() {
        IncomeRequest incomeRequest = IncomeTestFactory.createDefaultIncomeRequest();
        incomeRequest.setMonth(IncomeTestConstants.Invalid.YEAR_MONTH.toString());

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonthFormat() {
        IncomeRequest incomeRequest = IncomeTestFactory.createDefaultIncomeRequest();
        incomeRequest.setMonth(IncomeTestConstants.Invalid.YEAR_MONTH_FORMAT);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }
}
