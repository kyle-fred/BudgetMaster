package com.budgetmaster.dto;

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

import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.TransactionType;

public class IncomeRequestTest {    
    private static Validator validator;
    private IncomeRequest incomeRequest;
    private MoneyRequest moneyRequest;

    // -- Test Data --

    private String testName = "Test Income";
    private String testSource = "Test Source";
    private BigDecimal testAmount = new BigDecimal("100.00");
    private static final Currency GBP = Currency.getInstance("GBP");
    private TransactionType testType = TransactionType.ONE_TIME;
    private String testMonth = "2000-01";

    // -- Setup --

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUpRequest() {
        incomeRequest = new IncomeRequest();
        moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(testAmount);
        moneyRequest.setCurrency(GBP);
    }

    // -- Validation Tests --

    @Test
    void testValidIncomeRequest() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(testMonth);
        
        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullName() {
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals("Income name is required.", violations.iterator().next().getMessage());
    }

    @Test
    void testNullSource() {
        incomeRequest.setName(testName);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals("Income source is required.", violations.iterator().next().getMessage());
    }

    @Test
    void testNullMoney() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals("Money details (amount and currency) are required.", violations.iterator().next().getMessage());
    }

    @Test
    void testNullType() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals("Income transaction type is required.", violations.iterator().next().getMessage());
    }

    @Test
    void testNullMonth() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals("Month is required.", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonth() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth("2000-13");

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals("Month must be in format YYYY-MM.", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonthFormat() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth("2000/01");

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals("Month must be in format YYYY-MM.", violations.iterator().next().getMessage());
    }
}
