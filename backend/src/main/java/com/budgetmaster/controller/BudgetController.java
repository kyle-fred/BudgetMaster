package com.budgetmaster.controller;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.model.Budget;
import com.budgetmaster.service.BudgetService;

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
		Budget budget = budgetService.createBudget(request);
		return ResponseEntity.ok(budget);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
		Optional<Budget> budget = budgetService.getBudgetById(id);
		if (budget.isPresent()) {
			return ResponseEntity.ok(budget.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody BudgetRequest request) {
		Optional<Budget> updatedBudget = budgetService.updateBudget(id, request);
		
		return updatedBudget.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
		
	
}