package com.budgetmaster.dto.money;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.budgetmaster.test.constants.TestData.MoneyDtoTestData;
import com.budgetmaster.test.constants.TestData.SharedTestData;
import com.budgetmaster.test.constants.TestMessages.MoneyErrorMessages;

class MoneyRequestTest {
    // -- Dependencies --
    private static Validator validator;
    
    // -- Test Objects --
    private MoneyRequest request;

    // -- Test Data --
    private BigDecimal testAmount = MoneyDtoTestData.TEST_AMOUNT;
    private static final Currency GBP = SharedTestData.TEST_GBP;
    private static final Currency USD = MoneyDtoTestData.TEST_USD;

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
        assertEquals(MoneyErrorMessages.TEST_MESSAGE_MONEY_AMOUNT_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullCurrency() {
        request.setAmount(testAmount);

        Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(MoneyErrorMessages.TEST_MESSAGE_MONEY_CURRENCY_REQUIRED, violations.iterator().next().getMessage());
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
        assertEquals(MoneyErrorMessages.TEST_MESSAGE_MONEY_NEGATIVE_AMOUNT, violations.iterator().next().getMessage());
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
        request.setAmount(MoneyDtoTestData.TEST_LARGE_AMOUNT);
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