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
        Income income = incomeService.createIncome(request, null);
        return ResponseEntity.ok(income);
    }
    

    @PostMapping("/{monthYear}")
    public ResponseEntity<Income> createIncomeForMonth(@PathVariable String monthYear, @Valid @RequestBody IncomeRequest request) {
        Income income = incomeService.createIncome(request, monthYear);
        return ResponseEntity.ok(income);
    }
    
    @GetMapping("/{monthYear}")
    public ResponseEntity<List<Income>> getAllIncomesForMonth(@PathVariable String monthYear) {
        List<Income> incomes = incomeService.getAllIncomesForMonth(monthYear);
        return ResponseEntity.ok(incomes);
    }
	
    @GetMapping("/{monthYear}/{id}")
    public ResponseEntity<Income> getIncomeById(@PathVariable String monthYear, @PathVariable Long id) {
        Optional<Income> income = incomeService.getIncomeById(monthYear, id);
        if (income.isPresent()) {
        	return ResponseEntity.ok(income.get());
        } else {
        	return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{monthYear}/{id}")
    public ResponseEntity<Income> updateIncome(@PathVariable String monthYear, @PathVariable Long id, @Valid @RequestBody IncomeRequest request) {
        Optional<Income> income = incomeService.updateIncome(monthYear, id, request);
        if (income.isPresent()) {
        	return ResponseEntity.ok(income.get());
        } else {
        	return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{monthYear}/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable String monthYear, @PathVariable Long id) {
        boolean deleted = incomeService.deleteIncome(monthYear, id);
        
		if (deleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
    }
}