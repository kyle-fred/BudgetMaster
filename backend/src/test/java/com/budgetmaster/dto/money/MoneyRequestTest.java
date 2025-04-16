package com.budgetmaster.dto.money;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MoneyRequestTest {
    // -- Dependencies --
    private static Validator validator;
    
    // -- Test Objects --
    private MoneyRequest request;

    // -- Test Data --
    private BigDecimal testAmount = new BigDecimal("100.00");
    private static final Currency GBP = Currency.getInstance("GBP");

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
        assertEquals("Amount is required.", violations.iterator().next().getMessage());
    }

    @Test
    void testNullCurrency() {
        request.setAmount(testAmount);

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Currency must be provided", violations.iterator().next().getMessage());
    }

    @Test
    void testNegativeAmount() {
        request.setAmount(testAmount.negate());
        request.setCurrency(GBP);

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Amount must be non-negative.", violations.iterator().next().getMessage());
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
        request.setAmount(new BigDecimal("999999999999999.99"));
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