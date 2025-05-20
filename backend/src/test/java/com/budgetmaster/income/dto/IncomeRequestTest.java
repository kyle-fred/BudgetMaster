package com.budgetmaster.income.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.Messages;
import com.budgetmaster.testsupport.income.builder.IncomeRequestBuilder;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;

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
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();
        
        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullName() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withName(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.IncomeErrorMessageConstants.INCOME_NAME_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullSource() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withSource(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.IncomeErrorMessageConstants.INCOME_SOURCE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMoney() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withMoney(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.MoneyErrorMessageConstants.MONEY_DETAILS_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullType() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withType(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.IncomeErrorMessageConstants.INCOME_TYPE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMonth() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withMonth(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.MonthErrorMessageConstants.MONTH_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonth() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withMonth(IncomeConstants.Invalid.YEAR_MONTH.toString())
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonthFormat() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withMonth(IncomeConstants.Invalid.YEAR_MONTH_FORMAT)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }
}
