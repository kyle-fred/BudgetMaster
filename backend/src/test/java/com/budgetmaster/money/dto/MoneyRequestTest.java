package com.budgetmaster.money.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.budgetmaster.test.builder.MoneyRequestTestBuilder;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestData.MoneyTestConstants;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        MoneyRequest request = MoneyRequestTestBuilder.defaultIncome().buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullAmount() {
        MoneyRequest request = MoneyRequestTestBuilder.defaultIncome()
            .withAmount(null)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_AMOUNT_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullCurrency() {
        MoneyRequest request = MoneyRequestTestBuilder.defaultIncome()
            .withCurrency(null)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_CURRENCY_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testUnsupportedCurrency() {
        MoneyRequest request = MoneyRequestTestBuilder.defaultIncome()
            .withCurrency(MoneyTestConstants.InvalidValues.EUR)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_UNSUPPORTED_CURRENCY, violations.iterator().next().getMessage());
    }

    @Test
    void testNegativeAmount() {
        MoneyRequest request = MoneyRequestTestBuilder.defaultIncome()
            .withAmount(MoneyTestConstants.InvalidValues.AMOUNT)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_NEGATIVE_AMOUNT, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMoney() {
        MoneyRequest request = MoneyRequestTestBuilder.invalidMoney()
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());

        Set<String> messages = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());

        assertTrue(messages.contains(TestMessages.MoneyErrorMessageConstants.MONEY_NEGATIVE_AMOUNT));
        assertTrue(messages.contains(TestMessages.MoneyErrorMessageConstants.MONEY_UNSUPPORTED_CURRENCY));
    }

    @Test
    void testZeroAmount() {
        MoneyRequest request = MoneyRequestTestBuilder.defaultZero()
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testLargeAmount() {
        MoneyRequest request = MoneyRequestTestBuilder.defaultIncome()
            .withAmount(MoneyTestConstants.Miscellaneous.LARGE_AMOUNT)
            .buildRequest();

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        MoneyRequest request = MoneyRequestTestBuilder.defaultIncome()
            .buildRequest();

        assertEquals(MoneyTestConstants.IncomeDefaults.AMOUNT, request.getAmount());
        assertEquals(MoneyTestConstants.IncomeDefaults.CURRENCY, request.getCurrency());
    }
}