package com.budgetmaster.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.budgetmaster.common.constants.error.ErrorMessages;

@Documented
@Constraint(validatedBy = SupportedCurrencyValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedCurrencyConstraint {
    String message() default ErrorMessages.Currency.UNSUPPORTED_FOR_CONSTRAINT_ANNOTATION;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 