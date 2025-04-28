package com.budgetmaster.dto.money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import com.budgetmaster.test.constants.TestData;
import com.budgetmaster.test.constants.TestMessages;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyRequestTest {
    // -- Dependencies --
    private static Validator validator;
    
    // -- Test Objects --
    private MoneyRequest request;

    // -- Test Data --
    private BigDecimal testAmount = TestData.MoneyDtoTestDataConstants.AMOUNT;
    private static final Currency GBP = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
    private static final Currency USD = TestData.CurrencyTestDataConstants.CURRENCY_USD;

    // -- Setup --
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUpRequest() {
        request = new MoneyRequest();
    }

    // -- Validation Tests --

    @Test
    void testValidMoneyRequest() {
        request.setAmount(testAmount);
        request.setCurrency(GBP);

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullAmount() {
        request.setCurrency(GBP);

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_AMOUNT_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullCurrency() {
        request.setAmount(testAmount);

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_CURRENCY_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testUnsupportedCurrency() {
        request.setAmount(testAmount);
        request.setCurrency(USD);
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void testNegativeAmount() {
        request.setAmount(testAmount.negate());
        request.setCurrency(GBP);

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_NEGATIVE_AMOUNT, violations.iterator().next().getMessage());
    }

    @Test
    void testZeroAmount() {
        request.setAmount(BigDecimal.ZERO);
        request.setCurrency(GBP);

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testLargeAmount() {
        request.setAmount(TestData.MoneyDtoTestDataConstants.AMOUNT_LARGE);
        request.setCurrency(GBP);

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        request.setAmount(testAmount);
        request.setCurrency(GBP);

        assertEquals(testAmount, request.getAmount());
        assertEquals(GBP, request.getCurrency());
    }
}