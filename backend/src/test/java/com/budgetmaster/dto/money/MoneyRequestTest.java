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
    private static Validator validator;
    private static final Currency GBP = Currency.getInstance("GBP");
    private MoneyRequest request;

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
        request.setAmount(new BigDecimal("100.00"));
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
        request.setAmount(new BigDecimal("100.00"));

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Currency must be provided", violations.iterator().next().getMessage());
    }

    @Test
    void testNegativeAmount() {
        request.setAmount(new BigDecimal("-100.00"));
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
        BigDecimal amount = new BigDecimal("100.00");
        Currency currency = GBP;

        request.setAmount(amount);
        request.setCurrency(currency);

        assertEquals(amount, request.getAmount());
        assertEquals(currency, request.getCurrency());
    }
}