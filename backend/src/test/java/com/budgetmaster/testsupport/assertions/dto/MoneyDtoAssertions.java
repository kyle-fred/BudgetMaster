package com.budgetmaster.testsupport.assertions.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;

import com.budgetmaster.application.dto.MoneyRequest;

public class MoneyDtoAssertions {

  private final Set<ConstraintViolation<MoneyRequest>> violations;

  public MoneyDtoAssertions(Set<ConstraintViolation<MoneyRequest>> violations) {
    this.violations = violations;
  }

  public static MoneyDtoAssertions assertMoneyRequest(
      Set<ConstraintViolation<MoneyRequest>> violations) {
    return new MoneyDtoAssertions(violations);
  }

  public MoneyDtoAssertions hasNoViolations() {
    assertTrue(violations.isEmpty());
    return this;
  }

  public MoneyDtoAssertions hasExactlyOneViolation() {
    assertEquals(1, violations.size());
    return this;
  }

  public MoneyDtoAssertions hasViolationMessage(String expectedMessage) {
    assertEquals(expectedMessage, violations.iterator().next().getMessage());
    return this;
  }

  public MoneyDtoAssertions hasExactlyOneViolationMessage(String expectedMessage) {
    return hasExactlyOneViolation().hasViolationMessage(expectedMessage);
  }

  public MoneyDtoAssertions containsViolationMessage(String expectedMessage) {
    assertTrue(
        violations.stream()
            .anyMatch(violation -> violation.getMessage().contains(expectedMessage)));
    return this;
  }
}
