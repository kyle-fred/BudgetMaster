package com.budgetmaster.controller;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.model.Budget;
import com.budgetmaster.service.BudgetService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/budget")
public class BudgetController {
	private final BudgetService budgetService;
	
	public BudgetController(BudgetService budgetService) {
		this.budgetService = budgetService;
	}
	
	@PostMapping
	public Budget calculateBudget(@RequestBody BudgetRequest request) {
		return budgetService.calculateBudget(request);
	}
}