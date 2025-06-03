package com.budgetmaster.application.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.assertions.dto.ExpenseDtoAssertions;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

public class ExpenseRequestTest {

    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidExpenseRequest() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

        ExpenseDtoAssertions.assertExpenseRequest(violations)
            .hasNoViolations();
    }
    
    @Test
    void testNullName() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withName(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

        ExpenseDtoAssertions.assertExpenseRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Expense.NAME_REQUIRED);
    }

    @Test
    void testNullMoney() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withMoney(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

        ExpenseDtoAssertions.assertExpenseRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Money.DETAILS_REQUIRED);
    }

    @Test
    void testNullCategory() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withCategory(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

        ExpenseDtoAssertions.assertExpenseRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Expense.CATEGORY_REQUIRED);
    }

    @Test
    void testNullType() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withType(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

        ExpenseDtoAssertions.assertExpenseRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Expense.TYPE_REQUIRED);
    }

    @Test
    void testNullMonth() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withMonth(null)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

        ExpenseDtoAssertions.assertExpenseRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Month.REQUIRED);
    }

    @Test
    void testInvalidMonth() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withMonth(ExpenseConstants.Invalid.YEAR_MONTH.toString())
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

        ExpenseDtoAssertions.assertExpenseRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Month.INVALID_FORMAT);
    }

    @Test
    void testInvalidMonthFormat() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
            .withMonth(ExpenseConstants.Invalid.YEAR_MONTH_FORMAT)
            .buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

        ExpenseDtoAssertions.assertExpenseRequest(violations)
            .hasExactlyOneViolationMessage(ErrorConstants.Month.INVALID_FORMAT);
    }
}
