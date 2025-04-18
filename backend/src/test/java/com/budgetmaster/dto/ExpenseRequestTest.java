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
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.SupportedCurrency;
import com.budgetmaster.enums.TransactionType;

public class ExpenseRequestTest {
    // -- Dependencies --
    private static Validator validator;
    
    // -- Test Objects --
    private ExpenseRequest expenseRequest;
    private MoneyRequest moneyRequest;

    // -- Test Data --
    private String testName = "Test Expense";   
    private ExpenseCategory testCategory = ExpenseCategory.MISCELLANEOUS;
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
        assertTrue(violations.isEmpty(), "Violations should be empty for valid request");
    }
    
    @Test
    void testNullName() {
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);
        expenseRequest.setMonth(testMonth);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null name");
        assertEquals("Expense name is required.", violations.iterator().next().getMessage(), "Error message should be 'Expense name is required.'");
    }

    @Test
    void testNullMoney() {
        expenseRequest.setName(testName);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);
        expenseRequest.setMonth(testMonth);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null money");
        assertEquals("Money details (amount and currency) are required.", violations.iterator().next().getMessage());
    }

    @Test
    void testNullCategory() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setType(testType);
        expenseRequest.setMonth(testMonth);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null category");
        assertEquals("Expense category is required.", violations.iterator().next().getMessage(), "Error message should be 'Expense category is required.'");
    }

    @Test
    void testNullType() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setMonth(testMonth);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null type");
        assertEquals("Expense transaction type is required.", violations.iterator().next().getMessage(), "Error message should be 'Expense transaction type is required.'");
    }

    @Test
    void testNullMonth() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for null month");
        assertEquals("Month is required.", violations.iterator().next().getMessage(), "Error message should be 'Month is required.'");
    }

    @Test
    void testInvalidMonth() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);
        expenseRequest.setMonth("2000-13");

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for invalid month");
        assertEquals("Month must be in format YYYY-MM.", violations.iterator().next().getMessage(), "Error message should be 'Month must be in format YYYY-MM.'");
    }

    @Test
    void testInvalidMonthFormat() {
        expenseRequest.setName(testName);
        expenseRequest.setMoney(moneyRequest);
        expenseRequest.setCategory(testCategory);
        expenseRequest.setType(testType);
        expenseRequest.setMonth("2000/01");

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size(), "Violations should contain 1 error for invalid month format");
        assertEquals("Month must be in format YYYY-MM.", violations.iterator().next().getMessage(), "Error message should be 'Month must be in format YYYY-MM.'");
    }
}
