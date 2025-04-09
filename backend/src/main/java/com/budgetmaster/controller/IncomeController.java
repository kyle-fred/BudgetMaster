package com.budgetmaster.controller;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.model.Income;
import com.budgetmaster.service.IncomeService;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("api/incomes")
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
            @Pattern(regexp = "^\\d{4}-(?:0[1-9]|1[0-2])$", message = "Month year must be in format YYYY-MM") 
            String monthYear) {
        List<Income> incomes = incomeService.getAllIncomesForMonth(monthYear);
        return ResponseEntity.ok(incomes);
    }
	
    @GetMapping("/{id}")
    public ResponseEntity<Income> getIncomeById(@PathVariable Long id) {
        Optional<Income> income = incomeService.getIncomeById(id);
        if (income.isPresent()) {
            return ResponseEntity.ok(income.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Income> updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeRequest request) {
        Optional<Income> income = incomeService.updateIncome(id, request);
        if (income.isPresent()) {
            return ResponseEntity.ok(income.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        boolean deleted = incomeService.deleteIncome(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}