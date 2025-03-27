package com.budgetmaster.controller;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.model.Budget;
import com.budgetmaster.service.BudgetService;
import com.budgetmaster.utils.model.FinancialModelUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/budget")
@Validated
public class BudgetController {
	
	private final BudgetService budgetService;
	
	public BudgetController(BudgetService budgetService) {
		this.budgetService = budgetService;
	}
	
	@PostMapping
	public ResponseEntity<Budget> createBudget(@Valid @RequestBody BudgetRequest request) {
		BudgetRequest builtRequest = FinancialModelUtils.buildBudgetRequest(request);
		Budget budget = budgetService.createBudget(builtRequest);
		return ResponseEntity.ok(budget);
	}
	
	@GetMapping
	public ResponseEntity<Budget> getCurrentMonthBudget() {
		Budget budget = budgetService.getBudgetByMonthYear(null);
		return ResponseEntity.ok(budget);
	}
	
	@GetMapping("/{monthYear}")
	public ResponseEntity<Budget> getBudgetByMonth(@PathVariable String monthYear) {
		Budget budget = budgetService.getBudgetByMonthYear(monthYear);
		return ResponseEntity.ok(budget);
	}
	
	@PutMapping("/{monthYear}")
	public ResponseEntity<Budget> updateBudget(@PathVariable String monthYear, @Valid @RequestBody BudgetRequest request) {
		Budget budget = budgetService.updateBudget(monthYear, request);
		return ResponseEntity.ok(budget);
	}
	
	@DeleteMapping("/{monthYear}")
	public ResponseEntity<Void> deleteBudget(@PathVariable String monthYear) {
		budgetService.deleteBudget(monthYear);
		return ResponseEntity.noContent().build();
	}
}