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

import com.budgetmaster.testsupport.assertions.dto.IncomeDtoAssertions;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

@DisplayName("IncomeRequest Validation Tests")
class IncomeRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should validate a complete valid income request")
    void validateRequest_withValidData_hasNoViolations() {
        IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();
        
        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

        IncomeDtoAssertions.assertIncomeRequest(violations)
            .hasNoViolations();
    }

    @Nested
    @DisplayName("Required Field Validations")
    class RequiredFieldValidations {
        
        @Test
        @DisplayName("Should reject when name is null")
        void validateName_whenNull_hasNameRequiredViolation() {
            IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
                .withName(null)
                .buildRequest();

            Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

            IncomeDtoAssertions.assertIncomeRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Income.NAME_REQUIRED);
        }

        @Test
        @DisplayName("Should reject when source is null")
        void validateSource_whenNull_hasSourceRequiredViolation() {
            IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
                .withSource(null)
                .buildRequest();

            Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

            IncomeDtoAssertions.assertIncomeRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Income.SOURCE_REQUIRED);
        }

        @Test
        @DisplayName("Should reject when money details are null")
        void validateMoney_whenNull_hasMoneyRequiredViolation() {
            IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
                .withMoney(null)
                .buildRequest();

            Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

            IncomeDtoAssertions.assertIncomeRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Money.DETAILS_REQUIRED);
        }

        @Test
        @DisplayName("Should reject when type is null")
        void validateType_whenNull_hasTypeRequiredViolation() {
            IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
                .withType(null)
                .buildRequest();

            Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

            IncomeDtoAssertions.assertIncomeRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Income.TYPE_REQUIRED);
        }
    }

    @Nested
    @DisplayName("Month Field Validations")
    class MonthFieldValidations {
        
        @Test
        @DisplayName("Should reject when month is null")
        void validateMonth_whenNull_hasMonthRequiredViolation() {
            IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
                .withMonth(null)
                .buildRequest();

            Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

            IncomeDtoAssertions.assertIncomeRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Month.REQUIRED);
        }

        @Test
        @DisplayName("Should reject when month is invalid")
        void validateMonth_whenInvalid_hasInvalidFormatViolation() {
            IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
                .withMonth(IncomeConstants.Invalid.YEAR_MONTH.toString())
                .buildRequest();

            Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

            IncomeDtoAssertions.assertIncomeRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Month.INVALID_FORMAT);
        }

        @Test
        @DisplayName("Should reject when month format is incorrect")
        void validateMonth_whenFormatIncorrect_hasInvalidFormatViolation() {
            IncomeRequest incomeRequest = IncomeRequestBuilder.defaultIncomeRequest()
                .withMonth(IncomeConstants.Invalid.YEAR_MONTH_FORMAT)
                .buildRequest();

            Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(incomeRequest);

            IncomeDtoAssertions.assertIncomeRequest(violations)
                .hasExactlyOneViolationMessage(ErrorConstants.Month.INVALID_FORMAT);
        }
    }
}
