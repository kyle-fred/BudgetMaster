package com.budgetmaster.application.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.service.IncomeService;
import com.budgetmaster.constants.api.ApiMessages;
import com.budgetmaster.constants.api.ApiPaths;
import com.budgetmaster.constants.validation.ValidationPatterns;

@RestController
@RequestMapping(ApiPaths.Incomes.ROOT)
@Validated
public class IncomeController {

  private final IncomeService incomeService;

  public IncomeController(IncomeService incomeService) {
    this.incomeService = incomeService;
  }

  @PostMapping
  public ResponseEntity<Income> createIncome(@Valid @RequestBody IncomeRequest request) {
    Income income = incomeService.createIncome(request);
    return ResponseEntity.ok(income);
  }

  @GetMapping
  public ResponseEntity<List<Income>> getAllIncomesForMonth(
      @RequestParam
          @Pattern(
              regexp = ValidationPatterns.Date.YEAR_MONTH_REGEX,
              message = ApiMessages.ValidationMessages.MONTH_FORMAT_INVALID)
          String month) {
    List<Income> incomes = incomeService.getAllIncomesForMonth(month);
    return ResponseEntity.ok(incomes);
  }

  @GetMapping(ApiPaths.SEARCH_BY_ID)
  public ResponseEntity<Income> getIncomeById(@PathVariable Long id) {
    Income income = incomeService.getIncomeById(id);
    return ResponseEntity.ok(income);
  }

  @PutMapping(ApiPaths.SEARCH_BY_ID)
  public ResponseEntity<Income> updateIncome(
      @PathVariable Long id, @Valid @RequestBody IncomeRequest request) {
    Income income = incomeService.updateIncome(id, request);
    return ResponseEntity.ok(income);
  }

  @DeleteMapping(ApiPaths.SEARCH_BY_ID)
  public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
    incomeService.deleteIncome(id);
    return ResponseEntity.noContent().build();
  }
}
