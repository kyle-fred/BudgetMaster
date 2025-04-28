package com.budgetmaster.dto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
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

public class ExpenseRequestTest {
    // -- Dependencies --
    private static Validator validator;
    
    // -- Test Objects --
    private ExpenseRequest expenseRequest;
    private MoneyRequest moneyRequest;

    // -- Test Data --
    private String testName = TestData.ExpenseTestDataConstants.NAME;   
    private ExpenseCategory testCategory = TestData.ExpenseTestDataConstants.CATEGORY_MISCELLANEOUS;
    private BigDecimal testAmount = TestData.ExpenseTestDataConstants.AMOUNT;
    private static final Currency GBP = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
    private TransactionType testType = TestData.ExpenseTestDataConstants.TYPE_ONE_TIME;
    private String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;
    
    // -- Setup --
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUpRequest() {
        expenseRequest = new ExpenseRequest();
        moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(testAmount);
        moneyRequest.setCurrency(GBP);
    }

    // -- Validation Tests --

    @Test
    void testValidExpenseRequest() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);
        expenseRequest.setMonth(testMonth);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void testNullName() {
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);
        expenseRequest.setMonth(testMonth);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NAME_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMoney() {
        expenseRequest.setName(testName);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);
        expenseRequest.setMonth(testMonth);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_DETAILS_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullCategory() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setType(testType);
        expenseRequest.setMonth(testMonth);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.ExpenseErrorMessageConstants.EXPENSE_CATEGORY_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullType() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setMonth(testMonth);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.ExpenseErrorMessageConstants.EXPENSE_TYPE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMonth() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonth() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);
        expenseRequest.setMonth(TestData.MonthTestDataConstants.MONTH_INVALID);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonthFormat() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);
        expenseRequest.setMonth(TestData.MonthTestDataConstants.MONTH_INVALID_FORMAT);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }
}
