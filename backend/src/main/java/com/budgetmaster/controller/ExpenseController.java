package com.budgetmaster.controller;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.model.Expense;
import com.budgetmaster.service.ExpenseService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("api/expenses")
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
    
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpensesForMonth(
    		@RequestParam 
    		@Pattern(regexp = "^\\d{4}-(?:0[1-9]|1[0-2])$", message = "Month must be in format YYYY-MM") 
    		String month) {
        List<Expense> expenses = expenseService.getAllExpensesForMonth(month);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }
	
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        Expense expense = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(expense);
    }
	
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
	}
}