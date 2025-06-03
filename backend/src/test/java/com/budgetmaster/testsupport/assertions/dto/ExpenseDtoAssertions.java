package com.budgetmaster.testsupport.assertions.dto;

import jakarta.validation.ConstraintViolation;
import java.util.Set;

import com.budgetmaster.application.dto.ExpenseRequest;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseDtoAssertions {

    private final Set<ConstraintViolation<ExpenseRequest>> violations;

    public ExpenseDtoAssertions(Set<ConstraintViolation<ExpenseRequest>> violations) {
        this.violations = violations;
    }

    public static ExpenseDtoAssertions assertExpenseRequest(Set<ConstraintViolation<ExpenseRequest>> violations) {
        return new ExpenseDtoAssertions(violations);
    }

    public ExpenseDtoAssertions hasNoViolations() {
        assertTrue(violations.isEmpty());
        return this;
    }

    public ExpenseDtoAssertions hasExactlyOneViolation() {
        assertEquals(1, violations.size());
        return this;
    }

    public ExpenseDtoAssertions hasViolationMessage(String expectedMessage) {
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
        return this;
    }

    public ExpenseDtoAssertions hasExactlyOneViolationMessage(String expectedMessage) {
        return hasExactlyOneViolation()
            .hasViolationMessage(expectedMessage);
    }
}
