package com.budgetmaster.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.budgetmaster.application.dto.MoneyRequest;
import com.budgetmaster.application.enums.SupportedCurrency;

public class SupportedCurrencyValidator
    implements ConstraintValidator<SupportedCurrencyConstraint, MoneyRequest> {
  @Override
  public boolean isValid(MoneyRequest value, ConstraintValidatorContext context) {
    if (value == null || value.getCurrency() == null) {
      // Let MoneyRequest's @NotNull handle this case
      return true;
    }
    return SupportedCurrency.validateSupportedCurrency(value.getCurrency());
  }
}
