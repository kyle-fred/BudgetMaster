package com.budgetmaster.income.dto;

import java.util.Set;

import com.budgetmaster.test.builder.IncomeRequestTestBuilder;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestData.IncomeTestConstants;

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
        IncomeRequest incomeRequest = IncomeRequestTestBuilder.defaultIncomeRequest().buildRequest();
        
        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullName() {
        IncomeRequest incomeRequest = IncomeRequestTestBuilder.defaultIncomeRequest()
            .withName(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.IncomeErrorMessageConstants.INCOME_NAME_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullSource() {
        IncomeRequest incomeRequest = IncomeRequestTestBuilder.defaultIncomeRequest()
            .withSource(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.IncomeErrorMessageConstants.INCOME_SOURCE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMoney() {
        IncomeRequest incomeRequest = IncomeRequestTestBuilder.defaultIncomeRequest()
            .withMoney(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_DETAILS_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullType() {
        IncomeRequest incomeRequest = IncomeRequestTestBuilder.defaultIncomeRequest()
            .withType(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.IncomeErrorMessageConstants.INCOME_TYPE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMonth() {
        IncomeRequest incomeRequest = IncomeRequestTestBuilder.defaultIncomeRequest()
            .withMonth(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonth() {
        IncomeRequest incomeRequest = IncomeRequestTestBuilder.defaultIncomeRequest()
            .withMonth(IncomeTestConstants.Invalid.YEAR_MONTH.toString())
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonthFormat() {
        IncomeRequest incomeRequest = IncomeRequestTestBuilder.defaultIncomeRequest()
            .withMonth(IncomeTestConstants.Invalid.YEAR_MONTH_FORMAT)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }
}
