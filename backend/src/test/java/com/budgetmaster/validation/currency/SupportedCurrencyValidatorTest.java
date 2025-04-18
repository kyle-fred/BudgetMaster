package com.budgetmaster.validation.currency;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import com.budgetmaster.dto.money.MoneyRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupportedCurrencyValidatorTest {
    // -- Dependencies --
    private static Validator validator;
    
    // -- Test Objects --
    private MoneyRequest request;

    // -- Test Data --
    private static final Currency GBP = Currency.getInstance("GBP");
    private static final Currency USD = Currency.getInstance("USD");
    private static final BigDecimal TEST_AMOUNT = new BigDecimal("100.00");

    // -- Setup --
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUpRequest() {
        request = new MoneyRequest();
        request.setAmount(TEST_AMOUNT);
    }

    // -- Validation Tests --

    @Test
    void testValidCurrency() {
        request.setCurrency(GBP);
        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Should not have violations for supported currency");
    }

    @Test
    void testUnsupportedCurrency() {
        request.setCurrency(USD);
        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Should have one violation for unsupported currency");
        assertEquals("This currency type is not supported yet", violations.iterator().next().getMessage(),
            "Should have correct error message for unsupported currency");
    }

    @Test
    void testNullCurrency() {
        // Null currency should be handled by @NotNull on the field
        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Should have one violation for null currency");
        assertEquals("Currency must be provided", violations.iterator().next().getMessage(),
            "Should have correct error message for null currency");
    }
} 