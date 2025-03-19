package com.budgetmaster.controller;

import com.budgetmaster.model.Budget;
import com.budgetmaster.service.BudgetService;
import com.budgetmaster.utils.date.DateUtils;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/budget")
@Validated
public class BudgetController {
	
	private final BudgetService budgetService;
	
	public BudgetController(BudgetService budgetService) {
		this.budgetService = budgetService;
	}
	
	@GetMapping("/{monthYear}")
	public ResponseEntity<Budget> getBudgetById(@PathVariable String monthYear) {
		YearMonth yearMonth = DateUtils.parseYearMonth(monthYear);
		
		Optional<Budget> budget = budgetService.getBudgetByMonthYear(yearMonth);
		if (budget.isPresent()) {
			return ResponseEntity.ok(budget.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping("/{monthYear}")
	public ResponseEntity<Void> deleteBudget(@PathVariable String monthYear) {
		YearMonth yearMonth = DateUtils.parseYearMonth(monthYear); 
		boolean deleted = budgetService.deleteBudgetByMonthYear(yearMonth);
		
		if (deleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}