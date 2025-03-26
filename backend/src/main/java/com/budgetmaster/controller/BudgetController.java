package com.budgetmaster.controller;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.model.Budget;
import com.budgetmaster.service.BudgetService;
import com.budgetmaster.utils.model.FinancialModelUtils;

import java.util.Optional;

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
		Optional<Budget> budget = budgetService.getBudgetByMonthYear(null);
		if (budget.isPresent()) {
			return ResponseEntity.ok(budget.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/{monthYear}")
	public ResponseEntity<Budget> getBudgetByMonth(@PathVariable String monthYear) {
		
		Optional<Budget> budget = budgetService.getBudgetByMonthYear(monthYear);
		if (budget.isPresent()) {
			return ResponseEntity.ok(budget.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/{monthYear}")
	public ResponseEntity<Budget> updateBudget(@PathVariable String monthYear, @Valid @RequestBody BudgetRequest request) {
		Optional<Budget> budget = budgetService.updateBudget(monthYear, request);
		
		if (budget.isPresent()) {
			return ResponseEntity.ok(budget.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping("/{monthYear}")
	public ResponseEntity<Void> deleteBudget(@PathVariable String monthYear) {
		boolean deleted = budgetService.deleteBudget(monthYear);
		
		if (deleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}