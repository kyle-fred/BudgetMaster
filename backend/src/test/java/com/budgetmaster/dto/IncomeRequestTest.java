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
import com.budgetmaster.enums.SupportedCurrency;
import com.budgetmaster.enums.TransactionType;

public class IncomeRequestTest {    
    // -- Dependencies --
    private static Validator validator;
    
    // -- Test Objects --
    private IncomeRequest incomeRequest;
    private MoneyRequest moneyRequest;

    // -- Test Data --
    private String testName = "Test Income";
    private String testSource = "Test Source";
    private BigDecimal testAmount = new BigDecimal("100.00");
    private static final Currency GBP = SupportedCurrency.GBP.getCurrency();
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
        assertTrue(violations.isEmpty(), "Violations should be empty for valid request");
    }

    @Test
    void testNullName() {
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null name");
        assertEquals("Income name is required.", violations.iterator().next().getMessage(), "Error message should be 'Income name is required.'");
    }

    @Test
    void testNullSource() {
        incomeRequest.setName(testName);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null source");
        assertEquals("Income source is required.", violations.iterator().next().getMessage(), "Error message should be 'Income source is required.'");
    }

    @Test
    void testNullMoney() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null money");
        assertEquals("Money details (amount and currency) are required.", violations.iterator().next().getMessage(), "Error message should be 'Money details (amount and currency) are required.'");
    }

    @Test
    void testNullType() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null type");
        assertEquals("Income transaction type is required.", violations.iterator().next().getMessage(), "Error message should be 'Income transaction type is required.'");
    }

    @Test
    void testNullMonth() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null month");
        assertEquals("Month is required.", violations.iterator().next().getMessage(), "Error message should be 'Month is required.'");
    }

    @Test
    void testInvalidMonth() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth("2000-13");

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for invalid month");
        assertEquals("Month must be in format YYYY-MM.", violations.iterator().next().getMessage(), "Error message should be 'Month must be in format YYYY-MM.'");
    }

    @Test
    void testInvalidMonthFormat() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth("2000/01");

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for invalid month format");
        assertEquals("Month must be in format YYYY-MM.", violations.iterator().next().getMessage(), "Error message should be 'Month must be in format YYYY-MM.'");
    }
}
