package com.budgetmaster.application.dto;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.Error;
import com.budgetmaster.testsupport.money.builder.MoneyRequestBuilder;
import com.budgetmaster.testsupport.money.constants.MoneyConstants;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyRequestTest {
    // -- Dependencies --
    private static Validator validator;

    // -- Setup --
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidMoneyRequest() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome().buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullAmount() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withAmount(null)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(Error.Money.AMOUNT_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullCurrency() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withCurrency(null)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(Error.Money.CURRENCY_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testUnsupportedCurrency() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withCurrency(MoneyConstants.InvalidValues.EUR)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(Error.Money.UNSUPPORTED_CURRENCY, violations.iterator().next().getMessage());
    }

    @Test
    void testNegativeAmount() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withAmount(MoneyConstants.InvalidValues.AMOUNT)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(Error.Money.NEGATIVE_AMOUNT, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMoney() {
        MoneyRequest request = MoneyRequestBuilder.invalidMoney()
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());

        Set<String> messages = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());

        assertTrue(messages.contains(Error.Money.NEGATIVE_AMOUNT));
        assertTrue(messages.contains(Error.Money.UNSUPPORTED_CURRENCY));
    }

    @Test
    void testZeroAmount() {
        MoneyRequest request = MoneyRequestBuilder.defaultZero()
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testLargeAmount() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .withAmount(MoneyConstants.Miscellaneous.LARGE_AMOUNT)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        MoneyRequest request = MoneyRequestBuilder.defaultIncome()
            .buildRequest();

        assertEquals(MoneyConstants.IncomeDefaults.AMOUNT, request.getAmount());
        assertEquals(MoneyConstants.IncomeDefaults.CURRENCY, request.getCurrency());
    }
}