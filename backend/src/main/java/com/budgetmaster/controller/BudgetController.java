package com.budgetmaster.controller;

import com.budgetmaster.model.Budget;
import com.budgetmaster.service.BudgetService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("api/budgets")
@Validated
public class BudgetController {
	
	private final BudgetService budgetService;
	
	public BudgetController(BudgetService budgetService) {
		this.budgetService = budgetService;
	}
	
	@GetMapping
	public ResponseEntity<Budget> getBudgetByMonth(
			@RequestParam 
			@Pattern(regexp = "^\\d{4}-(?:0[1-9]|1[0-2])$", message = "Month must be in format YYYY-MM") 
			String month) {
		Budget budget = budgetService.getBudgetByMonth(month);
		return ResponseEntity.ok(budget);
	}
		
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
		budgetService.deleteBudget(id);
		return ResponseEntity.noContent().build();
	}
}