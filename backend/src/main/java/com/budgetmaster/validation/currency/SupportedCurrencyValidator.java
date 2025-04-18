package com.budgetmaster.validation.currency;

import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.SupportedCurrency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SupportedCurrencyValidator implements ConstraintValidator<SupportedCurrencyConstraint, MoneyRequest> {
    @Override
    public boolean isValid(MoneyRequest value, ConstraintValidatorContext context) {
        if (value == null || value.getCurrency() == null) {
            // Let MoneyRequest's @NotNull handle this case
            return true;
        }
        return SupportedCurrency.isSupported(value.getCurrency());
    }
} 