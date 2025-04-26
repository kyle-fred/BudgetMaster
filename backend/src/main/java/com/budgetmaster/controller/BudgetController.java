package com.budgetmaster.controller;

import com.budgetmaster.constants.api.ApiMessages;
import com.budgetmaster.constants.api.ApiPaths;
import com.budgetmaster.constants.validation.ValidationPatterns;
import com.budgetmaster.model.Budget;
import com.budgetmaster.service.BudgetService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping(ApiPaths.API_PATH_BASE_PATH + ApiPaths.API_PATH_BUDGETS)
@Validated
public class BudgetController {
	
	private final BudgetService budgetService;
	
	public BudgetController(BudgetService budgetService) {
		this.budgetService = budgetService;
	}
	
	@GetMapping
	public ResponseEntity<Budget> getBudgetByMonth(
			@RequestParam 
			@Pattern(regexp = ValidationPatterns.VALIDATION_PATTERN_YEAR_MONTH_REGEX, message = ApiMessages.API_MESSAGE_MONTH_FORMAT_INVALID) 
			String month) {
		Budget budget = budgetService.getBudgetByMonth(month);
		return ResponseEntity.ok(budget);
	}
		
	@DeleteMapping(ApiPaths.API_PATH_BY_ID)
	public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
		budgetService.deleteBudget(id);
		return ResponseEntity.noContent().build();
	}
}