package com.budgetmaster.expense.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.Messages;
import com.budgetmaster.testsupport.expense.builder.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.expense.constants.ExpenseConstants;

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
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void testNullName() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withName(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.ExpenseErrorMessageConstants.EXPENSE_NAME_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMoney() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withMoney(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.MoneyErrorMessageConstants.MONEY_DETAILS_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullCategory() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withCategory(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.ExpenseErrorMessageConstants.EXPENSE_CATEGORY_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullType() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withType(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.ExpenseErrorMessageConstants.EXPENSE_TYPE_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testNullMonth() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withMonth(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.MonthErrorMessageConstants.MONTH_REQUIRED, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonth() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withMonth(ExpenseConstants.Invalid.YEAR_MONTH.toString())
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidMonthFormat() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withMonth(ExpenseConstants.Invalid.YEAR_MONTH_FORMAT)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);
        assertEquals(1, violations.size());
        assertEquals(Messages.MonthErrorMessageConstants.MONTH_INVALID_FORMAT, violations.iterator().next().getMessage());
    }
}
