package com.budgetmaster.application.controller;

import jakarta.validation.constraints.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.service.BudgetService;
import com.budgetmaster.constants.api.ApiMessages;
import com.budgetmaster.constants.api.ApiPaths;
import com.budgetmaster.constants.validation.ValidationPatterns;

@RestController
@RequestMapping(ApiPaths.Budgets.ROOT)
@Validated
public class BudgetController {

  private final BudgetService budgetService;

  public BudgetController(BudgetService budgetService) {
    this.budgetService = budgetService;
  }

  @GetMapping
  public ResponseEntity<Budget> getBudgetByMonth(
      @RequestParam
          @Pattern(
              regexp = ValidationPatterns.Date.YEAR_MONTH_REGEX,
              message = ApiMessages.ValidationMessages.MONTH_FORMAT_INVALID)
          String month) {
    Budget budget = budgetService.getBudgetByMonth(month);
    return ResponseEntity.ok(budget);
  }

  @DeleteMapping(ApiPaths.SEARCH_BY_ID)
  public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
    budgetService.deleteBudget(id);
    return ResponseEntity.noContent().build();
  }
}
