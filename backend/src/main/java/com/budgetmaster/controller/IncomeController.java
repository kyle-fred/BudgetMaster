package com.budgetmaster.controller;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.model.Income;
import com.budgetmaster.service.IncomeService;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/income")
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
	
	@GetMapping("/{id}")
	public ResponseEntity<Income> getIncomeById(@PathVariable Long id) {
		Optional<Income> income= incomeService.getIncomeById(id);
		if (income.isPresent()) {
			return ResponseEntity.ok(income.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}