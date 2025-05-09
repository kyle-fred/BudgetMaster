package com.budgetmaster.expense.dto;

import java.util.Set;

import com.budgetmaster.test.builder.ExpenseRequestTestBuilder;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestData.ExpenseTestConstants;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseRequestTest {
    // -- Dependencies --
    private static Validator validator;
    
    // -- Setup --
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidExpenseRequest() {
        ExpenseRequest expenseRequest = ExpenseRequestTestBuilder.defaultExpenseRequest().buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void testNullName() {
        ExpenseRequest expenseRequest = ExpenseRequestTestBuilder.defaultExpenseRequest()
            .withName(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.ExpenseErrorMessageConstants.EXPENSE_NAME_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMoney() {
        ExpenseRequest expenseRequest = ExpenseRequestTestBuilder.defaultExpenseRequest()
            .withMoney(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MoneyErrorMessageConstants.MONEY_DETAILS_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullCategory() {
        ExpenseRequest expenseRequest = ExpenseRequestTestBuilder.defaultExpenseRequest()
            .withCategory(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.ExpenseErrorMessageConstants.EXPENSE_CATEGORY_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullType() {
        ExpenseRequest expenseRequest = ExpenseRequestTestBuilder.defaultExpenseRequest()
            .withType(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.ExpenseErrorMessageConstants.EXPENSE_TYPE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMonth() {
        ExpenseRequest expenseRequest = ExpenseRequestTestBuilder.defaultExpenseRequest()
            .withMonth(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonth() {
        ExpenseRequest expenseRequest = ExpenseRequestTestBuilder.defaultExpenseRequest()
            .withMonth(ExpenseTestConstants.Invalid.YEAR_MONTH.toString())
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonthFormat() {
        ExpenseRequest expenseRequest = ExpenseRequestTestBuilder.defaultExpenseRequest()
            .withMonth(ExpenseTestConstants.Invalid.YEAR_MONTH_FORMAT)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(TestMessages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }
}
