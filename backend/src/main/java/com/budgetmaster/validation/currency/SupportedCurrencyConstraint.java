package com.budgetmaster.validation.currency;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SupportedCurrencyValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedCurrencyConstraint {
    String message() default "This currency type is not supported yet";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 