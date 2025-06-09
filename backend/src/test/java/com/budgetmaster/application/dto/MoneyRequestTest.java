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

import com.budgetmaster.testsupport.assertions.dto.MoneyDtoAssertions;
import com.budgetmaster.testsupport.builder.dto.MoneyRequestBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

@DisplayName("MoneyRequest Validation Tests")
class MoneyRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Should validate a complete valid money request")
  void validateRequest_withValidData_hasNoViolations() {
    MoneyRequest moneyRequest = MoneyRequestBuilder.defaultIncome().buildRequest();

    Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(moneyRequest);

    MoneyDtoAssertions.assertMoneyRequest(violations).hasNoViolations();
  }

  @Nested
  @DisplayName("Required Field Validations")
  class RequiredFieldValidations {

    @Test
    @DisplayName("Should reject when amount is null")
    void validateAmount_whenNull_hasAmountRequiredViolation() {
      MoneyRequest request = MoneyRequestBuilder.defaultIncome().withAmount(null).buildRequest();

      Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

      MoneyDtoAssertions.assertMoneyRequest(violations)
          .hasExactlyOneViolationMessage(ErrorConstants.Money.AMOUNT_IS_REQUIRED);
    }

    @Test
    @DisplayName("Should reject when currency is null")
    void validateCurrency_whenNull_hasCurrencyRequiredViolation() {
      MoneyRequest request = MoneyRequestBuilder.defaultIncome().withCurrency(null).buildRequest();

      Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

      MoneyDtoAssertions.assertMoneyRequest(violations)
          .hasExactlyOneViolationMessage(ErrorConstants.Money.CURRENCY_IS_REQUIRED);
    }
  }

  @Nested
  @DisplayName("Currency Validations")
  class CurrencyValidations {

    @Test
    @DisplayName("Should reject when currency is unsupported")
    void validateCurrency_whenUnsupported_hasUnsupportedCurrencyViolation() {
      MoneyRequest request =
          MoneyRequestBuilder.defaultIncome()
              .withCurrency(MoneyConstants.InvalidValues.EUR)
              .buildRequest();

      Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

      MoneyDtoAssertions.assertMoneyRequest(violations)
          .hasExactlyOneViolationMessage(ErrorConstants.Money.CURRENCY_IS_NOT_SUPPORTED);
    }
  }

  @Nested
  @DisplayName("Amount Validations")
  class AmountValidations {

    @Test
    @DisplayName("Should reject when amount is negative")
    void validateAmount_whenNegative_hasNegativeAmountViolation() {
      MoneyRequest request =
          MoneyRequestBuilder.defaultIncome()
              .withAmount(MoneyConstants.InvalidValues.AMOUNT)
              .buildRequest();

      Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

      MoneyDtoAssertions.assertMoneyRequest(violations)
          .hasExactlyOneViolationMessage(ErrorConstants.Money.AMOUNT_MUST_BE_NON_NEGATIVE);
    }

    @Test
    @DisplayName("Should accept when amount is zero")
    void validateAmount_whenZero_hasNoViolations() {
      MoneyRequest request = MoneyRequestBuilder.defaultZero().buildRequest();

      Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

      MoneyDtoAssertions.assertMoneyRequest(violations).hasNoViolations();
    }

    @Test
    @DisplayName("Should accept when amount is large")
    void validateAmount_whenLarge_hasNoViolations() {
      MoneyRequest request =
          MoneyRequestBuilder.defaultIncome()
              .withAmount(MoneyConstants.Miscellaneous.LARGE_AMOUNT)
              .buildRequest();

      Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

      MoneyDtoAssertions.assertMoneyRequest(violations).hasNoViolations();
    }
  }

  @Nested
  @DisplayName("Multiple Field Validations")
  class MultipleFieldValidations {

    @Test
    @DisplayName("Should reject when multiple fields are invalid")
    void validateRequest_withMultipleInvalidFields_hasMultipleViolations() {
      MoneyRequest request = MoneyRequestBuilder.invalidMoney().buildRequest();

      Set<ConstraintViolation<MoneyRequest>> violations = validator.validate(request);

      MoneyDtoAssertions.assertMoneyRequest(violations)
          .containsViolationMessage(ErrorConstants.Money.AMOUNT_MUST_BE_NON_NEGATIVE)
          .containsViolationMessage(ErrorConstants.Money.CURRENCY_IS_NOT_SUPPORTED);
    }
  }
}
