package com.budgetmaster.dto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.test.constants.TestData;
import com.budgetmaster.test.constants.TestMessages;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IncomeRequestTest {    
    // -- Dependencies --
    private static Validator validator;
    
    // -- Test Objects --
    private IncomeRequest incomeRequest;
    private MoneyRequest moneyRequest;

    // -- Test Data --
    private String testName = TestData.IncomeTestDataConstants.NAME;
    private String testSource = TestData.IncomeTestDataConstants.SOURCE;
    private BigDecimal testAmount = TestData.IncomeTestDataConstants.AMOUNT;
    private static final Currency GBP = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
    private TransactionType testType = TestData.IncomeTestDataConstants.TYPE_ONE_TIME;
    private String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;

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
        assertEquals(TestMessages.IncomeErrorMessageConstants.INCOME_NAME_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullSource() {
        incomeRequest.setName(testName);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.IncomeErrorMessageConstants.INCOME_SOURCE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMoney() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_DETAILS_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullType() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setMonth(testMonth);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.IncomeErrorMessageConstants.INCOME_TYPE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMonth() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonth() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(TestData.MonthTestDataConstants.MONTH_INVALID);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonthFormat() {
        incomeRequest.setName(testName);
        incomeRequest.setSource(testSource);
        incomeRequest.setMoney(moneyRequest);
        incomeRequest.setType(testType);
        incomeRequest.setMonth(TestData.MonthTestDataConstants.MONTH_INVALID_FORMAT);

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }
}
