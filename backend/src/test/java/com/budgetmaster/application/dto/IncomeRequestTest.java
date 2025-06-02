package com.budgetmaster.application.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.assertions.dto.IncomeDtoAssertions;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

public class IncomeRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidIncomeRequest() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .buildRequest();
        
        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

        IncomeDtoAssertions.assertIncomeRequest(violations)
            .hasNoViolations();
    }

    @Test
    void testNullName() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withName(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

        IncomeDtoAssertions.assertIncomeRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Income.NAME_REQUIRED);
    }

    @Test
    void testNullSource() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withSource(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

        IncomeDtoAssertions.assertIncomeRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Income.SOURCE_REQUIRED);
    }

    @Test
    void testNullMoney() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withMoney(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

        IncomeDtoAssertions.assertIncomeRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Money.DETAILS_REQUIRED);
    }

    @Test
    void testNullType() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withType(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

        IncomeDtoAssertions.assertIncomeRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Income.TYPE_REQUIRED);
    }

    @Test
    void testNullMonth() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withMonth(null)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

        IncomeDtoAssertions.assertIncomeRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Month.REQUIRED);
    }

    @Test
    void testInvalidMonth() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withMonth(IncomeConstants.Invalid.YEAR_MONTH.toString())
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

        IncomeDtoAssertions.assertIncomeRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Month.INVALID_FORMAT);
    }

    @Test
    void testInvalidMonthFormat() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
            .withMonth(IncomeConstants.Invalid.YEAR_MONTH_FORMAT)
            .buildRequest();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

        IncomeDtoAssertions.assertIncomeRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Month.INVALID_FORMAT);
    }
}
