package com.budgetmaster.controller;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.model.Expense;
import com.budgetmaster.service.ExpenseService;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/expense")
@Validated
public class ExpenseController {
	
	private final ExpenseService expenseService;
	
	public ExpenseController(ExpenseService expenseService) {
		this.expenseService = expenseService;
	}
	
	@PostMapping
	public ResponseEntity<Expense> createExpense(@Valid @RequestBody ExpenseRequest request) {
		Expense expense = expenseService.createExpense(request);
		return ResponseEntity.ok(expense);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
		Optional<Expense> expense= expenseService.getExpenseById(id);
		if (expense.isPresent()) {
			return ResponseEntity.ok(expense.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
		Optional<Expense> expense = expenseService.updateExpense(id, request);
		
		if (expense.isPresent()) {
			return ResponseEntity.ok(expense.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
		boolean deleted = expenseService.deleteExpense(id);
		
		if (deleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}