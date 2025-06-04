package com.budgetmaster.application.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.assertions.dto.ExpenseDtoAssertions;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

@DisplayName("ExpenseRequest Validation Tests")
class ExpenseRequestTest {

    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should validate a complete valid expense request")
    void validateRequest_withValidData_hasNoViolations() {
        ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

        ExpenseDtoAssertions.assertExpenseRequest(violations)
            .hasNoViolations();
    }

    @Nested
    @DisplayName("Required Field Validations")
    class RequiredFieldValidations {
        
        @Test
        @DisplayName("Should reject when name is null")
        void validateName_whenNull_hasNameRequiredViolation() {
            ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
                .withName(null)
                .buildRequest();

            Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

            ExpenseDtoAssertions.assertExpenseRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Expense.NAME_REQUIRED);
        }

        @Test
        @DisplayName("Should reject when money details are null")
        void validateMoney_whenNull_hasMoneyRequiredViolation() {
            ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
                .withMoney(null)
                .buildRequest();

            Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

            ExpenseDtoAssertions.assertExpenseRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Money.DETAILS_REQUIRED);
        }

        @Test
        @DisplayName("Should reject when category is null")
        void validateCategory_whenNull_hasCategoryRequiredViolation() {
            ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
                .withCategory(null)
                .buildRequest();

            Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

            ExpenseDtoAssertions.assertExpenseRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Expense.CATEGORY_REQUIRED);
        }

        @Test
        @DisplayName("Should reject when type is null")
        void validateType_whenNull_hasTypeRequiredViolation() {
            ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
                .withType(null)
                .buildRequest();

            Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

            ExpenseDtoAssertions.assertExpenseRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Expense.TYPE_REQUIRED);
        }
    }

    @Nested
    @DisplayName("Month Field Validations")
    class MonthFieldValidations {
        
        @Test
        @DisplayName("Should reject when month is null")
        void validateMonth_whenNull_hasMonthRequiredViolation() {
            ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
                .withMonth(null)
                .buildRequest();

            Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

            ExpenseDtoAssertions.assertExpenseRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Month.REQUIRED);
        }

        @Test
        @DisplayName("Should reject when month is invalid")
        void validateMonth_whenInvalid_hasInvalidFormatViolation() {
            ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
                .withMonth(ExpenseConstants.Invalid.YEAR_MONTH.toString())
                .buildRequest();

            Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

            ExpenseDtoAssertions.assertExpenseRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Month.INVALID_FORMAT);
        }

        @Test
        @DisplayName("Should reject when month format is incorrect")
        void validateMonth_whenFormatIncorrect_hasInvalidFormatViolation() {
            ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest()
                .withMonth(ExpenseConstants.Invalid.YEAR_MONTH_FORMAT)
                .buildRequest();

            Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(expenseRequest);

            ExpenseDtoAssertions.assertExpenseRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Month.INVALID_FORMAT);
        }
    }
}
