package com.budgetmaster.controller;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.model.Budget;
import com.budgetmaster.service.BudgetService;
import com.budgetmaster.utils.model.FinancialModelUtils;

import java.time.YearMonth;
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
	
	@GetMapping("/{year}/{month}")
	public ResponseEntity<Budget> getBudgetById(@PathVariable int year, int month) {
		YearMonth monthYear = YearMonth.of(year, month);
		
		Optional<Budget> budget = budgetService.getBudgetByMonthYear(monthYear);
		if (budget.isPresent()) {
			return ResponseEntity.ok(budget.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetRequest request) {
		Optional<Budget> budget = budgetService.updateBudget(id, request);
		
		if (budget.isPresent()) {
			return ResponseEntity.ok(budget.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
		boolean deleted = budgetService.deleteBudget(id);
		
		if (deleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}