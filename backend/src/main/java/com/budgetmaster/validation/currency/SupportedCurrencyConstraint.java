package com.budgetmaster.validation.currency;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.budgetmaster.constants.error.ErrorMessages.CurrencyErrorMessages;

@Documented
@Constraint(validatedBy = SupportedCurrencyValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedCurrencyConstraint {
    String message() default CurrencyErrorMessages.ERROR_MESSAGE_UNSUPPORTED_CURRENCY_FOR_CONSTRAINT_ANNOTATION;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 