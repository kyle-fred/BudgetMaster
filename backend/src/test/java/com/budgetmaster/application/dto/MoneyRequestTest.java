package com.budgetmaster.application.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.assertions.dto.MoneyDtoAssertions;
import com.budgetmaster.testsupport.builder.dto.MoneyRequestBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

public class MoneyRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidMoneyRequest() {
        MoneyRequest moneyRequest = MoneyRequestBuilder.defaultIncome().buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(moneyRequest);

        MoneyDtoAssertions.assertMoneyRequest(violations)
            .hasNoViolations();
    }

    @Test
    void testNullAmount() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withAmount(null)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        
        MoneyDtoAssertions.assertMoneyRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Money.AMOUNT_REQUIRED);
    }

    @Test
    void testNullCurrency() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withCurrency(null)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

        MoneyDtoAssertions.assertMoneyRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Money.CURRENCY_REQUIRED);
    }

    @Test
    void testUnsupportedCurrency() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withCurrency(MoneyConstants.InvalidValues.EUR)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

        MoneyDtoAssertions.assertMoneyRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Money.UNSUPPORTED_CURRENCY);
    }

    @Test
    void testNegativeAmount() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withAmount(MoneyConstants.InvalidValues.AMOUNT)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

        MoneyDtoAssertions.assertMoneyRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Money.NEGATIVE_AMOUNT);
    }

    @Test
    void testInvalidMoney() {
        MoneyRequest request = MoneyRequestBuilder.invalidMoney()
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        
        MoneyDtoAssertions.assertMoneyRequest(violations)
            .containsViolationMessage(ErrorConstants.Money.NEGATIVE_AMOUNT)
            .containsViolationMessage(ErrorConstants.Money.UNSUPPORTED_CURRENCY);
    }

    @Test
    void testZeroAmount() {
        MoneyRequest request = MoneyRequestBuilder.defaultZero()
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

        MoneyDtoAssertions.assertMoneyRequest(violations)
            .hasNoViolations();
    }

    @Test
    void testLargeAmount() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withAmount(MoneyConstants.Miscellaneous.LARGE_AMOUNT)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

        MoneyDtoAssertions.assertMoneyRequest(violations)
            .hasNoViolations();
    }
}