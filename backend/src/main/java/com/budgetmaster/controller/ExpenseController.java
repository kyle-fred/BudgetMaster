package com.budgetmaster.controller;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.model.Expense;
import com.budgetmaster.service.ExpenseService;

import java.util.List;
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
 		Expense expense = expenseService.createExpense(request, null);
		return ResponseEntity.ok(expense);
	}
	
    @PostMapping("/{monthYear}")
    public ResponseEntity<Expense> createExpenseForMonth(@PathVariable String monthYear, @Valid @RequestBody ExpenseRequest request) {
        Expense expense = expenseService.createExpense(request, monthYear);
        return ResponseEntity.ok(expense);
    }
    
    @GetMapping("/{monthYear}")
    public ResponseEntity<List<Expense>> getAllExpensesForMonth(@PathVariable String monthYear) {
        List<Expense> expenses = expenseService.getAllExpensesForMonth(monthYear);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/{monthYear}/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable String monthYear, @PathVariable Long id) {
        Optional<Expense> expense = expenseService.getExpenseById(monthYear, id);
        if (expense.isPresent()) {
        	return ResponseEntity.ok(expense.get());
        } else {
        	return ResponseEntity.notFound().build();
        }
    }
	
    @PutMapping("/{monthYear}/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable String monthYear, @PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        Optional<Expense> expense = expenseService.updateExpense(monthYear, id, request);
        if (expense.isPresent()) {
        	return ResponseEntity.ok(expense.get());
        } else {
        	return ResponseEntity.notFound().build();
        }
    }
	
    @DeleteMapping("/{monthYear}/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String monthYear, @PathVariable Long id) {
        boolean deleted = expenseService.deleteExpense(monthYear, id);
        
		if (deleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}