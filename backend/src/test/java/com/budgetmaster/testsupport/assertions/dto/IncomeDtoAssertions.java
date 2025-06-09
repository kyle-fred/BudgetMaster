package com.budgetmaster.testsupport.assertions.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;

import com.budgetmaster.application.dto.IncomeRequest;

public class IncomeDtoAssertions {

  private final Set<ConstraintViolation<IncomeRequest>> violations;

  public IncomeDtoAssertions(Set<ConstraintViolation<IncomeRequest>> violations) {
    this.violations = violations;
  }

  public static IncomeDtoAssertions assertIncomeRequest(
      Set<ConstraintViolation<IncomeRequest>> violations) {
    return new IncomeDtoAssertions(violations);
  }

  public IncomeDtoAssertions hasNoViolations() {
    assertTrue(violations.isEmpty());
    return this;
  }

  public IncomeDtoAssertions hasExactlyOneViolation() {
    assertEquals(1, violations.size());
    return this;
  }

  public IncomeDtoAssertions hasViolationMessage(String expectedMessage) {
    assertEquals(expectedMessage, violations.iterator().next().getMessage());
    return this;
  }

  public IncomeDtoAssertions hasExactlyOneViolationMessage(String expectedMessage) {
    return hasExactlyOneViolation().hasViolationMessage(expectedMessage);
  }
}
